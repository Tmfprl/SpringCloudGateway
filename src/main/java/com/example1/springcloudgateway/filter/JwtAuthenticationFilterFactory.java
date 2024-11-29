package com.example1.springcloudgateway.filter;

import com.example1.springcloudgateway.config.TokenProvider;
import com.example1.springcloudgateway.service.GatewayLoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtAuthenticationFilterFactory extends AbstractGatewayFilterFactory<JwtAuthenticationFilterFactory.Config> {

    private final TokenProvider tokenProvider;
    private final GatewayLoginService loginService;
    private final ServerProperties serverProperties;

    public JwtAuthenticationFilterFactory(TokenProvider tokenProvider, GatewayLoginService loginService, ServerProperties serverProperties) {
        super(Config.class);
        this.tokenProvider = tokenProvider;
        this.loginService = loginService;
        this.serverProperties = serverProperties;
    }

    // 토큰 검증
    @Override
    public GatewayFilter apply(Config config) {     // Spring WebFlux에서 요청과 응답의 생명주기를 관리하고, 요청/응답의 데이터에 접근 및 조작을 가능하게 한다. (리액티브 프로그래밍 모델을 지원하는 웹 프레임워크)
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            // 토큰이 헤더에 포함되어있을 경우
            String token = request.getHeaders().getFirst("Authorization");

            log.info("request PATH : {} ", request.getURI().getPath());

            // URL에 토큰이 포함되어 전달되는 경우이다.
            // String token = request.getQueryParams().getFirst("token");
            log.info("token : {}", token);
            if (token == null || token.isEmpty()) {
                log.info("Token is empty. Requesting new token....");
                // 없는 경우 토큰 발급
//                response.setStatusCode(HttpStatus.SEE_OTHER);
//                response.getHeaders().setLocation(URI.create("http://localhost:8083/login"));
                // 이러면 그냥 요청만 하는거 잖아 게이트웨이의 역할을 못하잖아 8083 쪽에서 게이트웨이 요청 url 을 지정 할 수 없어 클라이언트 요청이 뭔지 알고 서비스에서 그런 처리를 해 못하잖아 바보야
//                return response.setComplete();
                return loginService.requestToken()
                        // flatMap()을 사용하면 순차적으로 로직을 처릴 할 수 있다 (then() 처럼)
                        // map과 flatMap은 둘 다 스트림의 중간에 값을 변환해주는 역할을 한다.
                        // map은 1 : 1로 반환을 보증하고 flatMap은 1 : N을 변환할 수 있다.
                        .flatMap(newToken -> {
                            // 토큰 검증 후 헤더에 추가
                            ServerHttpRequest modifiedRequest = exchange.getRequest()
                                    .mutate()
                                    .header("Authorization", "Bearer " + newToken)
                                    .build();
                            return chain.filter(exchange.mutate().request(modifiedRequest).build());
                        })
                        // onErrorReturn은 Reactive Stream에서 Error가 발생했을 경우 정해진 Fallback value를 리턴해주는 방식입니다.
                        .onErrorResume(error -> {
                            log.error("Error during token fetching: {}", error.getMessage());
                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            return exchange.getResponse().setComplete();
                        });
            } else if (!tokenProvider.validateToken(token)) {
                log.info("Token is not valid. Requesting new token....");
                // 만료된 경우 재발급
//                response.setStatusCode(HttpStatus.BAD_REQUEST);
//                response.getHeaders().setLocation(URI.create("http://localhost:8083/refresh-token?token=" + token));
//                return response.setComplete();
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

            // JSON 타입으로 바디안에 담겨서 토큰이 전달되는 경우

        };
    }

    public static class Config {
        // 필터 설정에 필요한 파라미터를 정의 (예: 로깅 활성화 여부)
        // 필수로 설정해야 하는 것은 아니다 \(0ㅁ0 )
    }

}


