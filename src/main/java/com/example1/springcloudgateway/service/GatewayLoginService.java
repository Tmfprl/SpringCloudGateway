package com.example1.springcloudgateway.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
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

    /**
     * 로그인 요청후에 토큰을 전달받아 추출한다.
     *
     * @return
     */
    public Mono<String> requestToken() {
        log.info("request token...");

        return webClient.post()
                .uri("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
//                .body(BodyInserters.fromFormData("username", "yourUsername")
//                        .with("password", "yourPassword"))
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().is2xxSuccessful()) {
                        log.info("request token success");
                        // 헤더에서 토큰 추출
                        String token = clientResponse.headers()
                                .asHttpHeaders()
                                .getFirst("Authorization");
                        if (token != null) {
                            log.info("request token null");
                            return Mono.just(token);
                        }
                    }
                    return Mono.error(new RuntimeException("Failed to fetch token"));
                });
    }
}

// 여기다 이럴게 아닌가 ...? 라우팅 설정 부분에 설정을 해놔야하나? 아닌 듯 그니까 요청을 보내면 get 방식이나 post 방식으로 보내겠지 그러면 로그인 폼을 통해 로그인 정보를 받지 못하니까 문제가 생기는 거같은데
// 근데 웹 클라이언트는 restemplate 같은 거라서 컨트롤거 같은 기능인데 ... 그럼 요청하는 부부ㄴ말고 반환하는 부분에서 아애 토큰을 반환해야하는데 어떻게?
// 근데 현재 로그인 서비스에서 로그인에 성공하면 flash 로 그냥 날려버리는데 이걸 잡아야 하나? 어 ...어...어...