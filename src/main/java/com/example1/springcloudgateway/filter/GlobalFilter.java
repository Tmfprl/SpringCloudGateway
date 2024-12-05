package com.example1.springcloudgateway.filter;

import com.example1.springcloudgateway.config.TokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.RedirectToGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalFilter.Config> {
    public GlobalFilter(RedirectToGatewayFilterFactory redirectToGatewayFilterFactory, TokenProvider tokenProvider) {
        super(Config.class);
        this.tokenProvider = tokenProvider;
    }
    String parameter;
    String token = null;
    private final TokenProvider tokenProvider;


    // apply를 재정의해야 하며 필터의 로직을 정의
    // chain.filter(exchange)를 호출하여 다음 필터로 체인을 전달하기 전에 필요한 작업을 수행
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // chain.filter()를 리턴하기 전에 요청 조작과 관련된 코드를 추가
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("request information : {}, {}", request.getHeaders(), request.getURI());

            log.info("===============Global Filter Base Message : {}", config.getBaseMessage());
            if (config.isPreLogger()) {
                log.info("Global Filter Pre Request ID: {}", request.getId());
            }
            if (request.getQueryParams().isEmpty()) {
                return chain.filter(exchange);
            } else {
                request.getQueryParams().forEach((key, value) -> {
                    log.info("request parameter : {}", value);
                });
                String target = request.getQueryParams().values().toString();
                parameter = target.replace("[", "").replace("]", "");
                log.info("parameter : {}", parameter);
            }
            log.info("===============Global Pre Filter End");
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                log.info("===============Global Post Filter End");
                log.info("request information : {}, {}", request.getHeaders(), request.getURI());
                log.info("request Method : {}", request.getMethod());
            }));
        };
        // 필터와 관련된 설정값을 추가할 수 있음
    }

    @Data
    public static class Config {
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;
    }
}


