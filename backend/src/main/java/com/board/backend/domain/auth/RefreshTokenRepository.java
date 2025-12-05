package com.board.backend.domain.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // 토큰 문자열로 조회
    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    // 특정 유저의 유효한 토큰
    Optional<RefreshToken> findByUserIdAndIsRevokedFalse(Long userId);
}