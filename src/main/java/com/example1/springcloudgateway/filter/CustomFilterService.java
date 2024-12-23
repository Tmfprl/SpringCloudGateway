package com.example1.springcloudgateway.filter;

import com.example1.springcloudgateway.Exception.CustomException;
import com.example1.springcloudgateway.Exception.CustomExceptionHandler;
import com.example1.springcloudgateway.Exception.ExceptionCode;
import com.example1.springcloudgateway.config.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomFilterService {
    private String token;
    private final ExtractBody extractBody;
    private final TokenProvider tokenProvider;

    public String checkURI (String uri){
        String newUri;
        if(uri.contains("service1")){
            newUri = "http://192.168.56.2:8081/service1/tcDstrctMng?userId=";
        } else if(uri.contains("service2")){
            newUri = "http://192.168.56.2:8082/service2/tlMvmneqPass?userId=";
        } else {
            throw new CustomException(ExceptionCode.NOT_FOUND_URL);
        }
        return newUri;
    }

    // 요청을 처리하는 메서드
    public Mono<Void> handleRequest(ServerWebExchange exchange, GatewayFilterChain chain, ServerHttpRequest request, ServerHttpResponse response, String url) {
        return exchange.getRequest().getBody()
                .collectList()
                .flatMap(dataBuffers -> {
                    String body = dataBuffers.stream()
                            .map(buffer -> StandardCharsets.UTF_8.decode(buffer.asByteBuffer()).toString())
                            .reduce("", String::concat);
                    log.info("Request Body: {}", body);

                    String username = extractBody.extractValueFromBody(body, "username");
                    String password = extractBody.extractValueFromBody(body, "password");
                    log.info("Username: {}, Password : {}", username, password);

                    return authenticateUser(username, password, response, url)
                            .flatMap(isAuthenticated -> {
                                if (isAuthenticated) {
                                    return chain.filter(exchange); // 인증 성공 시 체인 계속 진행
                                } else {
                                    return Mono.error(new IllegalArgumentException("Invalid token"));
                                }
                            });
                });
    }

    // 사용자 인증
    private Mono<Boolean> authenticateUser(String username, String password, ServerHttpResponse response, String url) {
        log.info("userinfo id : {}, password {}",username, password);
        return WebClient.create()
                .post()
//                .uri("http://192.168.56.2:8083/login")
                .uri("http://localhost:8083/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("username=" + username + "&password=" + password)
                .retrieve()
                .bodyToMono(String.class)
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

                        response.getHeaders().setLocation(URI.create(checkURI(url)+user));
                        return Mono.empty();
                    }
                    else {
                        return Mono.error(new IllegalArgumentException("Invalid token"));
                    }
                });
    }

}
