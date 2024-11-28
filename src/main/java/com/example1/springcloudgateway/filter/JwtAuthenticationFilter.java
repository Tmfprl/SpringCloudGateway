package com.example1.springcloudgateway.filter;

import com.example1.springcloudgateway.config.TokenProvider;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.token.TokenService;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


import javax.swing.*;
import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter implements GlobalFilter {
    private final TokenProvider tokenProvider;

    // 토큰 검증
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {//    Spring WebFlux에서 요청과 응답의 생명주기를 관리하고, 요청/응답의 데이터에 접근 및 조작을 가능하게 한다. (리액티브 프로그래밍 모델을 지원하는 웹 프레임워크)
        ServerHttpRequest request = exchange.getRequest();

        // 토큰을 (url생략..token=토큰)을 추출 해준다
        String token = request.getQueryParams().getFirst("token");
        if(token == null || token.isEmpty()) {
            return handleUnauthorized(exchange);
        }

        // 토큰 검증
        try {
            tokenProvider.validateToken(token);
        } catch (JwtException e) {
            return handleUnauthorized(exchange);
        }

        //

        return null;
    }

    private Mono<Void> handleUnauthorized(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        // 401 에러 처리
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }

}


