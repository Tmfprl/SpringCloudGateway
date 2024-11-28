package com.example1.springcloudgateway.filter;

import com.example1.springcloudgateway.config.TokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;


import java.net.URI;

@Slf4j
@Component
public class JwtAuthenticationFilterFactory extends AbstractGatewayFilterFactory<JwtAuthenticationFilterFactory.Config> {

    private final TokenProvider tokenProvider;

    public JwtAuthenticationFilterFactory(TokenProvider tokenProvider) {
        super(Config.class);
        this.tokenProvider = tokenProvider;
    }

    // 토큰 검증
    @Override
    public GatewayFilter apply(Config config) {//    Spring WebFlux에서 요청과 응답의 생명주기를 관리하고, 요청/응답의 데이터에 접근 및 조작을 가능하게 한다. (리액티브 프로그래밍 모델을 지원하는 웹 프레임워크)
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("request PATH : {} ", request.getURI().getPath());

            // 토큰을 (url생략..token=토큰)을 추출 해준다
            String token = request.getQueryParams().getFirst("token");
            log.info("token : {}", token);
            if (token == null || token.isEmpty()) {
                // 없는 경우 토큰 발급
                response.setStatusCode(HttpStatus.SEE_OTHER);
                response.getHeaders().setLocation(URI.create("http://localhost:8083/login"));
                return response.setComplete();
            } else if (!tokenProvider.validateToken(token)) {
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
            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        };
    }

    public static class Config {
        // 필터 설정에 필요한 파라미터를 정의 (예: 로깅 활성화 여부)
        // 필수로 설정해야 하는 것은 아니다 \(0ㅁ0 )
    }

}


