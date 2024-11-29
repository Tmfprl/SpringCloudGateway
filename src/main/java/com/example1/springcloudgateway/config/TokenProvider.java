package com.example1.springcloudgateway.config;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class TokenProvider {

    @Value("${token.secret}")
    private String TOKEN_SECRET;

    // 토큰 검증
    public boolean validateToken(String token) {
        log.info("Validating token...");
        try {
            var claims = Jwts.parser()
                    // token decrypt (복호화)
                    .setSigningKey(TOKEN_SECRET)
                    .parseClaimsJws(token)
                    .getBody();
            // token expiration check (토큰 만료 확인)
            return !claims.getExpiration().before(new Date());
        } catch (JwtException e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
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
