package com.board.backend.domain.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoLoginRequest {
    private String accessToken;  // 카카오에서 받은 Access Token
}
