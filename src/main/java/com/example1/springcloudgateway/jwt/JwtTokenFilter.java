package com.example1.springcloudgateway.jwt;

import com.example1.springcloudgateway.exception.ServiceCoustomException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    final String AUTHORIZATION_HEADER = "access-token";
//    Bearer : JWT 혹은 OAuth에 대한 토큰을 사용한다. (RFC 6750)
//    public static final String BEARER_PREFIX = "Bearer ";
    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
         // 1. Request Header 에서 토큰을 꺼냄
        String jwt = response.getHeader(AUTHORIZATION_HEADER);
        log.info("response.getHeader(AUTHORIZATION_HEADER) : {}", jwt);

        try {
            if (jwt != null && tokenProvider.validateToken(jwt)) {
                Authentication auth = tokenProvider.getAuthentication(jwt);
                // 정상 토큰이면 토큰을 통해 생성한 Authentication 객체를 SecurityContext에 저장
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (ServiceCoustomException e) {
            SecurityContextHolder.clearContext();
            response.sendError(404, e.getMessage());
            return;
        }
        filterChain.doFilter(request, response);

    }
}