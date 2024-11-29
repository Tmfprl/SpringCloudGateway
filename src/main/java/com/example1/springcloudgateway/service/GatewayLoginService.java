package com.example1.springcloudgateway.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@Slf4j
public class GatewayLoginService {

    // WebCilent 는 이벤트 기반 애플리케이션을 구축하기위해 설계된 Spring WebFlux 프레임워크의 일부이며 기존의 RestTemplate 과는 달리
    // 비동기 비차단 방식으로 Http 요청을 처리하는 반응현 HTTP 클라이언트 이다.
    public final WebClient webClient;

    // GatewayLoginService 호출시에 로그인 요청 url이 나간다
    public GatewayLoginService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8083").build(); // 로그인 서비스 URL
    }

    /**
     * 로그인 요청후에 토큰을 전달받아 추출한다.
     *
     * @return
     */
    public static Mono<String> fetchToken(WebClient webClient) {
        log.info("json parse token : ");

        return webClient.get()
                .uri("/login") // 로그인 엔드포인트
                // ResponseEntity를 받아 디코딩
                .retrieve()
                // 4xx, 5xx 에러 처리
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), clientResponse -> Mono.error(new RuntimeException("Login failed")))
                .toBodilessEntity() // 응답 바디 대신 헤더 추출
                .mapNotNull(responseEntity -> responseEntity.getHeaders().getFirst("Authorization")); // 토큰 추출
    }
}

