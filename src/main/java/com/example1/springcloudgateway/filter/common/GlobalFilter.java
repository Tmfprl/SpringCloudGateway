package com.example1.springcloudgateway.filter.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalFilter.Config> {
    public GlobalFilter() {
        super(Config.class);
    }

    // 필터와 관련된 설정값을 추가할 수 있음
    @Getter @Setter
    @NoArgsConstructor
    public static class Config {
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;
    }

    // apply를 재정의해야 하며 필터의 로직을 정의
    // chain.filter(exchange)를 호출하여 다음 필터로 체인을 전달하기 전에 필요한 작업을 수행
    @Override
    public GatewayFilter apply(Config config) {
        //Custom Pre Filter
        return (exchange, chain) -> {

            // chain.filter()를 리턴하기 전에 요청 조작과 관련된 코드를 추가
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();
            log.info("request information : {}, {}", request.getHeaders(), request.getURI());

            log.info("Global Filter Base Message : {}", config.getBaseMessage());
            if (config.isPreLogger()) {
                log.info("Global Filter Pre Request ID: {}", request.getId());
            }

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                if (config.isPostLogger()) {
                    log.info("Global Filter Post Response Code: {}", response.getStatusCode());
                }

            }));
        };
    }
}
