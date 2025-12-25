package com.board.backend.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class OAuthController {

    private final OAuthService oAuthService;

    @PostMapping("/kakao/callback")
    public ResponseEntity<?> kakaoCallback(@RequestBody Map<String, String> body) {
        String code = body.get("code");
        Map<String, String> tokens = oAuthService.processKakaoLogin(code);

        return ResponseEntity.ok(tokens);
    }
}
