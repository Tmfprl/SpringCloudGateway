package com.example1.springcloudgateway.filter;

import com.example1.springcloudgateway.config.SecurityConfig;
import com.example1.springcloudgateway.jwt.TokenProvider;
import com.example1.springcloudgateway.user.service.GatewayLoginService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;

@Slf4j
@Component
public class JwtAuthenticationFilterFactory extends AbstractGatewayFilterFactory<JwtAuthenticationFilterFactory.Config> {

    private final TokenProvider tokenProvider;
    private final GatewayLoginService loginService;
    private final ServerProperties serverProperties;
    private final SecurityConfig securityConfig;

    public JwtAuthenticationFilterFactory(TokenProvider tokenProvider, GatewayLoginService loginService, ServerProperties serverProperties, SecurityConfig securityConfig) {
        super(Config.class);
        this.tokenProvider = tokenProvider;
        this.loginService = loginService;
        this.serverProperties = serverProperties;
        this.securityConfig = securityConfig;
    }


    // 토큰 검증
    @Override
    public GatewayFilter apply(Config config) {     // Spring WebFlux에서 요청과 응답의 생명주기를 관리하고, 요청/응답의 데이터에 접근 및 조작을 가능하게 한다. (리액티브 프로그래밍 모델을 지원하는 웹 프레임워크
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            if (config.isPreLogger()) {
                log.info("[GlobalFilter Start] request ID: {}, method: {}, path: {}", request.getId(), request.getMethod(), request.getPath());
            }

            String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            log.info("request PATH : {} ", request.getURI().getPath());
            if (token == null || token.isEmpty()) {
                log.info("Token is empty. Requesting new token....");
                // 로그인 서비스로 요청 보내기
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                response.setComplete();
                WebClient webClient = WebClient.create();
                return webClient.post()
                        .uri("http://localhost:8083/login")
                        .retrieve()
                        .bodyToMono(String.class)
                        .flatMap(loginResponse -> {
                            log.info("로그인 폼 응답: {}", loginResponse);
                            DataBuffer dataBuffer = new DefaultDataBufferFactory().wrap(loginResponse.getBytes());
                            return response.writeWith(Mono.just(dataBuffer));  // 응답 본문 작성

                        })
                        .onErrorResume(e -> {
                            log.error("로그인 요청 실패", e);
                            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                            return response.setComplete();
                        });

            } else if (!tokenProvider.validateToken(token)) {
                log.info("Token is not valid. Requesting new token....");
                // 만료된 경우 재발급
                response.setStatusCode(HttpStatus.BAD_REQUEST);
                response.getHeaders().setLocation(URI.create("http://localhost:8083/refresh-token?token=" + token));
                return response.setComplete();
            }
            log.info("token validate success");


            // 토큰 검증 후 실행
            String userId = tokenProvider.getUserIdFromToken(token);
            // 유저 아이디를 헤더에 넣어 service 쪽에서 확인
            ServerHttpRequest modifiedRequest = exchange.getRequest()
                    .mutate()
                    .header("user-id", userId)
                    .build();

            // 빌더를 사용하여 요청을 조작한 채로 다음 필터 체인으로 넘김(mutate 로 빌더를 다시 생성 할 수 있다.)
            return chain.filter(exchange.mutate().request(modifiedRequest).build()).then(Mono.fromRunnable(() -> {
                if (config.isPostLogger()) {
                    log.info("[GlobalFilter End  ] request ID: {}, method: {}, path: {}, statusCode: {}", request.getId(), request.getMethod(), request.getPath(), response.getStatusCode());
                }
            }));
        };
    }

    @Data
    public static class Config {
        // put the configure
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;
    }

}


