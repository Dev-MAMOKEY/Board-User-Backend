package com.board.backend.config;

import com.board.backend.security.jwt.JwtAuthenticationFilter;
import com.board.backend.security.OAuth2LoginSuccessHandler;
import com.board.backend.security.OAuth2LoginFailureHandler;
import com.board.backend.security.CustomOAuth2UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/auth/**",
                                "/login/**",
                                "/oauth2/**",
                                "/oauth2/authorization/**",
                                "/test/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET, "/posts", "/posts/*").permitAll()  // GET 게시글 조회만 인증 불필요
                        .anyRequest().authenticated()
                )

                // 인증/인가 예외 처리 (REST API용)
                .exceptionHandling(exception -> exception
                        // 인증 실패 시 401 JSON 응답 (리다이렉트 X)
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(401);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding("UTF-8");

                            Map<String, Object> errorResponse = new HashMap<>();
                            errorResponse.put("error", "Unauthorized");
                            errorResponse.put("message", "인증이 필요합니다");
                            errorResponse.put("path", request.getRequestURI());

                            ObjectMapper mapper = new ObjectMapper();
                            response.getWriter().write(mapper.writeValueAsString(errorResponse));
                        })
                        // 권한 없음 시 403 JSON 응답
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(403);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding("UTF-8");

                            Map<String, Object> errorResponse = new HashMap<>();
                            errorResponse.put("error", "Forbidden");
                            errorResponse.put("message", "접근 권한이 없습니다");
                            errorResponse.put("path", request.getRequestURI());

                            ObjectMapper mapper = new ObjectMapper();
                            response.getWriter().write(mapper.writeValueAsString(errorResponse));
                        })
                )

                // OAuth2 로그인 처리 (웹 테스트용)
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo ->
                                userInfo.userService(customOAuth2UserService))
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureHandler(oAuth2LoginFailureHandler)
                )

                // JWT 필터 적용
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
