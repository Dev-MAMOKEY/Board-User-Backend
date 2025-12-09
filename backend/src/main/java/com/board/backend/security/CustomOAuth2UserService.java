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
        // 1) OAuth2로부터 사용자 정보 가져오기
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 2) 카카오 사용자 정보 추출
        Map<String, Object> attributes = oAuth2User.getAttributes();
        Long kakaoId = (Long) attributes.get("id");
        String snsId = "kakao_" + kakaoId;

        // 3) 카카오 계정 정보 추출
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        final String email;
        if (kakaoAccount != null && kakaoAccount.containsKey("email")) {
            email = (String) kakaoAccount.get("email");
        } else {
            email = null;
        }

        // 4) DB에서 기존 사용자 조회 또는 신규 생성
        User user = userRepository.findBySnsId(snsId)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .snsId(snsId)
                            .email(email)
                            .provider(Provider.KAKAO)
                            .role(Role.USER)
                            .build();
                    return userRepository.save(newUser);
                });

        // 5) Custom OAuth2User 반환 (userId를 포함)
        return new CustomOAuth2User(user.getId(), user.getRole().name(), attributes);
    }
}
