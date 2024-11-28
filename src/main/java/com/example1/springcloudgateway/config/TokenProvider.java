package com.example1.springcloudgateway.config;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TokenProvider {

    @Value("${token.secret}")
    private String TOKEN_SECRET;

    // 토큰 검증
    public void validateToken(String token) {
        log.info("token validate check");
        Jwts.parser().setSigningKey(TOKEN_SECRET).parseClaimsJws(token);
    }


    // 토큰에서 사용자 ID 추출
    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(TOKEN_SECRET)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject(); // 토큰에 설정된 userId(소유자) 반환
    }
}
