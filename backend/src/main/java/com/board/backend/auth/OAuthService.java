package com.board.backend.auth;

import com.board.backend.domain.user.Provider;
import com.board.backend.domain.user.Role;
import com.board.backend.domain.user.User;
import com.board.backend.domain.user.UserRepository;
import com.board.backend.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OAuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirectUri;

    private final String KAKAO_TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private final String KAKAO_USERINFO_URL = "https://kapi.kakao.com/v2/user/me";

    public Map<String, String> processKakaoLogin(String code) {

        // 1) 카카오 토큰 발급
        String accessToken = getKakaoAccessToken(code);

        // 2) 카카오 사용자 정보 조회
        Map<String, Object> userInfo = getKakaoUserInfo(accessToken);

        String snsId = String.valueOf(userInfo.get("id"));
        String email = (String) userInfo.get("email");

        // 3) DB에서 사용자 조회, 없으면 생성
        User user = userRepository.findBySnsId(snsId)
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .provider(Provider.KAKAO)
                                .role(Role.USER)
                                .snsId(snsId)
                                .email(email)
                                .build()
                ));

        // 4) JWT 발급
        String accessTokenJwt = jwtUtil.generateAccessToken(
                user.getId(),
                user.getRole().name()
        );
        String refreshTokenJwt = jwtUtil.generateRefreshToken(user.getId());

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessTokenJwt);
        tokens.put("refreshToken", refreshTokenJwt);

        return tokens;
    }

    private String getKakaoAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Map<String, String> params = new HashMap<>();
        params.put("grant_type", "authorization_code");
        params.put("client_id", clientId);
        params.put("client_secret", clientSecret);
        params.put("redirect_uri", redirectUri);
        params.put("code", code);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(params, headers);

        Map<String, Object> response = restTemplate.postForObject(KAKAO_TOKEN_URL, request, Map.class);

        return (String) response.get("access_token");
    }

    private Map<String, Object> getKakaoUserInfo(String kakaoAccessToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + kakaoAccessToken);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                KAKAO_USERINFO_URL,
                HttpMethod.POST,
                entity,
                Map.class
        );

        Map<String, Object> body = response.getBody();
        Map<String, Object> account = (Map<String, Object>) body.get("kakao_account");

        Map<String, Object> result = new HashMap<>();
        result.put("id", body.get("id"));
        result.put("email", account.get("email"));

        return result;
    }
}
