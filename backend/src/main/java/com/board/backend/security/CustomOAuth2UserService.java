package com.board.backend.security;

import com.board.backend.domain.user.Provider;
import com.board.backend.domain.user.Role;
import com.board.backend.domain.user.User;
import com.board.backend.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        try {
            // 1) OAuth2 기본 정보 가져오기
            OAuth2User oAuth2User = super.loadUser(userRequest);

            // 2) 카카오 attributes
            Map<String, Object> attributes = oAuth2User.getAttributes();
            log.info("Kakao OAuth2 attributes: {}", attributes);

            // 3) kakao id 안전하게 추출
            Object idObj = attributes.get("id");
            if (idObj == null) {
                throw new IllegalArgumentException("Kakao ID를 찾을 수 없습니다.");
            }
            Long kakaoId = Long.valueOf(String.valueOf(idObj));
            String snsId = "kakao_" + kakaoId;

            log.info("Kakao snsId: {}", snsId);

            // 4) 이메일 추출 (옵션)
            final String email = extractEmail(attributes);
            log.info("Kakao email: {}", email);

            // 5) DB 조회 or 신규 생성
            User user = userRepository.findBySnsId(snsId)
                    .orElseGet(() -> {
                        log.info("신규 사용자 생성: {}", snsId);
                        return userRepository.save(
                                User.builder()
                                        .snsId(snsId)
                                        .email(email)
                                        .provider(Provider.KAKAO)
                                        .role(Role.USER)
                                        .build()
                        );
                    });

            log.info("사용자 로그인 성공 - userId: {}, role: {}", user.getId(), user.getRole());

            // 6) CustomOAuth2User 반환
            return new CustomOAuth2User(
                    user.getId(),
                    user.getRole().name(),
                    attributes
            );

        } catch (Exception e) {
            log.error("OAuth2 로그인 처리 중 에러 발생", e);
            throw new OAuth2AuthenticationException("OAuth2 로그인 실패: " + e.getMessage());
        }
    }

    private String extractEmail(Map<String, Object> attributes) {
        try {
            Object kakaoAccountObj = attributes.get("kakao_account");
            if (kakaoAccountObj instanceof Map) {
                Map<String, Object> kakaoAccount = (Map<String, Object>) kakaoAccountObj;
                return (String) kakaoAccount.get("email");
            }
        } catch (Exception e) {
            log.warn("이메일 추출 실패: {}", e.getMessage());
        }
        return null;
    }
}
