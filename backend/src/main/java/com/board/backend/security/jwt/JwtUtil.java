package com.board.backend.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key key;

    // application.yml에서 secret 값 불러오기
    public JwtUtil(@Value("${jwt.secret}") String secretKey) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // Access Token 만료시간 (30분)
    private static final long ACCESS_EXPIRATION = 1000L * 60 * 30;

    // Refresh Token 만료시간 (14일)
    private static final long REFRESH_EXPIRATION = 1000L * 60 * 60 * 24 * 14;


    /** 🟦 Access Token 생성 */
    public String generateAccessToken(Long userId, String role) {
        Date now = new Date();
        Date expire = new Date(now.getTime() + ACCESS_EXPIRATION);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expire)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /** 🟩 Refresh Token 생성 */
    public String generateRefreshToken(Long userId) {
        Date now = new Date();
        Date expire = new Date(now.getTime() + REFRESH_EXPIRATION);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(expire)
                .setId(java.util.UUID.randomUUID().toString())
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /** 🟨 JWT에서 userId 꺼내기 */
    public Long getUserId(String token) {
        Claims claims = parseClaims(token);
        return Long.valueOf(claims.getSubject());
    }

    /** 🟧 JWT에서 role 꺼내기 */
    public String getRole(String token) {
        Claims claims = parseClaims(token);
        return claims.get("role", String.class);
    }

    /** 🟥 토큰 만료 여부 */
    public boolean isExpired(String token) {
        try {
            Date expiration = parseClaims(token).getExpiration();
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    /** ⚪ 파싱 및 검증 */
    private Claims parseClaims(String token) {
        return Jwts.parser()                      // parserBuilder()가 아니라 parser()
                .verifyWith((SecretKey) key)                 // verifyWith()로 검증키 설정
                .build()
                .parseSignedClaims(token)        // parseClaimsJws() → parseSignedClaims()
                .getPayload();
    }
}
