package com.board.backend.domain.auth;

import com.board.backend.domain.auth.dto.RefreshTokenResponse;
import com.board.backend.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

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
}
