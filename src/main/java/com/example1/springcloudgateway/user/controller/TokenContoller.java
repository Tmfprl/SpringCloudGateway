package com.example1.springcloudgateway.user.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
@RequestMapping("/token")
public class TokenContoller {

    private final Map<String, String> tokenStorage = new ConcurrentHashMap<>();

    @PostMapping
    public ResponseEntity<Void> saveToken(@RequestBody Map<String, String> payload) {
        log.info("save token");
        String token = payload.get("accessToken");
        if (token != null && !token.isEmpty()) {
            // 사용자 세션 ID나 특정 키와 매핑하여 저장
             String username = payload.get("username");
            tokenStorage.put(username, token);
            log.info("save token success");
            return ResponseEntity.ok().build();
        }
        log.error("save token failed");
        return ResponseEntity.badRequest().build();
    }
}
