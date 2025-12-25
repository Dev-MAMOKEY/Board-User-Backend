package com.board.backend.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            return null;
        }

        // JwtAuthenticationFilter에서 principal에 userId(long)를 넣어놨음
        return Long.valueOf(authentication.getPrincipal().toString());
    }
}
