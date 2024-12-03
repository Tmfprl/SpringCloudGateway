package com.example1.springcloudgateway.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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

    public Mono<String> requestToken() {
        log.info("request token...");

        return webClient.get()
            .uri("http://localhost:8083/login")
                .retrieve()
                .bodyToMono(String.class)
                .handle((response, sink) -> {
                    // JSON에서 토큰 추출
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        log.info("response : {}", mapper.writeValueAsString(response));
                        String token = mapper.readTree(response).get("accessToken").asText();
                        if(token.isEmpty()) {
                            sink.error(new NullPointerException());
                            return;
                        }
                        sink.next(token);
                    } catch (JsonProcessingException e) {
                        sink.error(new RuntimeException(e));
                    }
                });
    }
}