package com.board.backend.security;

import com.board.backend.domain.user.Provider;
import com.board.backend.domain.user.Role;
import com.board.backend.domain.user.User;
import com.board.backend.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1) OAuth2 기본 정보 가져오기
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 2) 카카오 attributes
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 3) kakao id 안전하게 추출
        Object idObj = attributes.get("id");
        Long kakaoId = Long.valueOf(String.valueOf(idObj));

        String snsId = "kakao_" + kakaoId;

        // 4) 이메일 추출 (옵션)
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        String email = kakaoAccount != null ? (String) kakaoAccount.get("email") : null;

        // 5) DB 조회 or 신규 생성
        User user = userRepository.findBySnsId(snsId)
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .snsId(snsId)
                                .email(email)
                                .provider(Provider.KAKAO)
                                .role(Role.USER)
                                .build()
                ));

        // 6) CustomOAuth2User 반환
        return new CustomOAuth2User(
                user.getId(),
                user.getRole().name(),
                attributes
        );
    }
}
