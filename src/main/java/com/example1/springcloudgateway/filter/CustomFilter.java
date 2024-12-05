package com.example1.springcloudgateway.filter;

import com.example1.springcloudgateway.config.TokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class CustomFilter extends AbstractGatewayFilterFactory<CustomFilter.Config> {

    private String token;
    private final TokenProvider tokenProvider;
    final ExtractBody extractBody;

    public CustomFilter(TokenProvider tokenProvider, ExtractBody extractBody) {
        super(Config.class);
        this.tokenProvider = tokenProvider;
        this.extractBody = extractBody;
    }
// 그런데 결국 request 는 요청되지 않은 거잖아
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // Pre process start ====================================================
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("Custom Filter: request uri -> {}", request.getId());

            // Pre process end =======================================================
            return exchange.getRequest().getBody()
                    .collectList()
                    .flatMap(dataBuffers -> {
                        // 요청 바디를 읽기
                        String body = dataBuffers.stream()
                                .map(buffer -> StandardCharsets.UTF_8.decode(buffer.asByteBuffer()).toString())
                                .reduce("", String::concat);

                        log.info("Request Body: {}", body);

                        // body에서 username과 password 추출 (파싱 작업 필요)
                        String username = extractBody.extractValueFromBody(body, "username");
                        String password = extractBody.extractValueFromBody(body, "password");


                        if (username == null || password == null) {
                            return Mono.error(new IllegalArgumentException("Invalid login information"));
                        }

                        if(!request.getPath().toString().contains("/login")){
                            return Mono.error(new IllegalArgumentException("Invalid login information"));
                        }

                        // WebClient로 로그인 요청 전송
                         return WebClient.create()
                            .post()
                            .uri("http://localhost:8083/login")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .bodyValue("username=" + username + "&password=" + password)
                            .retrieve()
                            .bodyToMono(String.class)
//                            .doOnNext(responseBody ->
//                                    log.info("응답: {}", responseBody))
//                            .then();
//                            .flatMap(f -> {
//                               return chain.filter(exchange.mutate().request(request).build());
//                            });
                            .flatMap(responseBody -> {
                                // 응답에서 accessToken 추출
                                token = extractBody.extractTokenFromBody(responseBody, "accessToken");
                                log.info("Extracted Token: {}", token);

                                if (token == null) {
                                    return Mono.error(new IllegalArgumentException("Token not found in the response"));
                                }

                                // 토큰 검증 로직 추가 (예: JWT 검증)
                                if ((tokenProvider.validateToken(token))) {
                                    String user = tokenProvider.getUserIdFromToken(token);
                                    response.setStatusCode(HttpStatus.FOUND);
                                    log.info("Extracted User: {}", user);
                                    response.getHeaders().add(HttpHeaders.AUTHORIZATION, "X-USERID " + user);
                                    response.getHeaders().setLocation(URI.create("http://localhost:8081/service1/tcDstrctMng?userId="+user));
                                    return Mono.empty();
                                }
//                                        log.info("Token validated");
//                                        log.info("request: {}", request.getURI());
//                                        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
//                                            log.info("Post chain action executed after token validation");
//                                        }));
//                                    }
                                    else {
                                        return Mono.error(new IllegalArgumentException("Invalid token"));
                                    }
                                });
                    });

        };
    }


    public static class Config {
    }
}
