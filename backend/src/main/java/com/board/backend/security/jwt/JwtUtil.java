package com.board.backend.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKeyString;

    @Value("${jwt.access-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshTokenExpiration;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        byte[] keyBytes = secretKeyString.getBytes(StandardCharsets.UTF_8);

        // HMAC-SHA256은 최소 32바이트(256비트) 필요
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException(
                "JWT secret key must be at least 32 bytes (256 bits) long. Current length: " + keyBytes.length
            );
        }

        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    // **** Access Token 생성 ****
    public String generateAccessToken(Long userId, String role) {
        return createToken(userId, role, accessTokenExpiration);
    }

    // **** Refresh Token 생성 ****
    public String generateRefreshToken(Long userId, String role) {
        return createToken(userId, role, refreshTokenExpiration);
    }

    // 내부 공통 토큰 생성 메서드
    private String createToken(Long userId, String role, long expiredMs) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiredMs);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    // **** 토큰에서 userId 꺼내기 ****
    public Long getUserId(String token) {
        try {
            String subject = parseClaims(token).getSubject();
            if (subject == null) {
                throw new IllegalArgumentException("Token subject is null");
            }
            return Long.valueOf(subject);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid userId in token: " + e.getMessage(), e);
        }
    }

    // **** 토큰에서 role 꺼내기 ****
    public String getRole(String token) {
        return parseClaims(token).get("role", String.class);
    }

    // **** 토큰 유효성 검증 ****
    public boolean isValidToken(String token) {
        // null 또는 빈 문자열 체크
        if (!StringUtils.hasText(token)) {
            return false;
        }

        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            // 만료된 토큰
            return false;
        } catch (JwtException e) {
            // 잘못된 서명, 형식 오류 등
            return false;
        } catch (IllegalArgumentException e) {
            // 잘못된 인자
            return false;
        }
    }

    // **** Claims 파싱 (공통) ****
    private Claims parseClaims(String token) {
        if (!StringUtils.hasText(token)) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
