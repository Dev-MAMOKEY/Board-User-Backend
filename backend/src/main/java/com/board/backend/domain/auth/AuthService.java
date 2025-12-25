package com.board.backend.domain.auth;

import com.board.backend.auth.OAuthService;
import com.board.backend.domain.auth.dto.RefreshTokenResponse;
import com.board.backend.domain.auth.dto.TokenResponse;
import com.board.backend.domain.user.Provider;
import com.board.backend.domain.user.Role;
import com.board.backend.domain.user.User;
import com.board.backend.domain.user.UserRepository;
import com.board.backend.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final OAuthService oAuthService;
    private final JwtUtil jwtUtil;

    @Value("${jwt.refresh-expiration}")
    private long refreshTokenExpiration;

    public RefreshTokenResponse refreshAccessToken(String refreshToken) {

        // 1. DB에서 refresh token 조회
        RefreshToken tokenEntity = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 Refresh Token 입니다."));

        // 2. 폐기 여부 체크
        if (tokenEntity.isRevoked()) {
            throw new IllegalStateException("이미 사용된 Refresh Token 입니다.");
        }

        // 3. 만료 체크
        if (tokenEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Refresh Token 이 만료되었습니다.");
        }

        // 4. 새로운 Access Token 발급
        Long userId = tokenEntity.getUser().getId();
        String role = tokenEntity.getUser().getRole().name();

        String newAccessToken = jwtUtil.generateAccessToken(userId, role);

        return new RefreshTokenResponse(newAccessToken);
    }

    public void logout(String refreshToken) {

        RefreshToken tokenEntity = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 Refresh Token 입니다."));

        // 이미 폐기된 토큰인지 체크
        if (tokenEntity.isRevoked()) {
            throw new IllegalStateException("이미 로그아웃된 Refresh Token 입니다.");
        }

        // 토큰 폐기
        tokenEntity.setRevoked(true);
        refreshTokenRepository.save(tokenEntity);
    }

    public TokenResponse kakaoLogin(String kakaoAccessToken) {

        // 1. 카카오 Access Token으로 사용자 정보 조회
        Map<String, Object> userInfo = oAuthService.getKakaoUserInfo(kakaoAccessToken);

        Long kakaoId = ((Number) userInfo.get("id")).longValue();
        String snsId = "kakao_" + kakaoId;
        String email = (String) userInfo.get("email");

        // 2. DB에서 사용자 조회 또는 생성
        User user = userRepository.findBySnsId(snsId)
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .snsId(snsId)
                                .email(email)
                                .provider(Provider.KAKAO)
                                .role(Role.USER)
                                .build()
                ));

        // 3. JWT 토큰 생성
        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        // 4. Refresh Token DB 저장
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .refreshToken(refreshToken)
                .user(user)
                .expiresAt(LocalDateTime.now().plusSeconds(refreshTokenExpiration / 1000))
                .isRevoked(false)
                .build();

        refreshTokenRepository.save(refreshTokenEntity);

        // 5. 토큰 반환
        return new TokenResponse(accessToken, refreshToken);
    }

}
