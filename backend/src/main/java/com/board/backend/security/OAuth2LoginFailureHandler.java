package com.board.backend.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class OAuth2LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException, ServletException {

        log.error("========================================");
        log.error("OAuth2 로그인 실패!");
        log.error("에러 메시지: {}", exception.getMessage());
        log.error("에러 타입: {}", exception.getClass().getName());
        log.error("상세 스택:", exception);
        log.error("========================================");

        // 에러 페이지로 리다이렉트 (에러 메시지 포함)
        String errorMessage = exception.getMessage();
        if (errorMessage == null) {
            errorMessage = "알 수 없는 오류";
        }

        getRedirectStrategy().sendRedirect(
                request,
                response,
                "/login?error&message=" + java.net.URLEncoder.encode(errorMessage, "UTF-8")
        );
    }
}
