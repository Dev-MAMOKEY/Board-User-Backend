package com.board.backend.domain.auth;

import com.board.backend.domain.auth.dto.KakaoLoginRequest;
import com.board.backend.domain.auth.dto.LogoutRequest;
import com.board.backend.domain.auth.dto.RefreshTokenRequest;
import com.board.backend.domain.auth.dto.RefreshTokenResponse;
import com.board.backend.domain.auth.dto.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/kakao/login")
    public TokenResponse kakaoLogin(@RequestBody KakaoLoginRequest request) {
        return authService.kakaoLogin(request.getAccessToken());
    }

    @PostMapping("/refresh")
    public RefreshTokenResponse refresh(@RequestBody RefreshTokenRequest request) {
        return authService.refreshAccessToken(request.getRefreshToken());
    }

    @PostMapping("/logout")
    public String logout(@RequestBody LogoutRequest request) {
        authService.logout(request.getRefreshToken());
        return "로그아웃 완료";
    }
}
