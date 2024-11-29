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
        log.info("token validate check");
        try {
            var claims = Jwts.parser().setSigningKey(TOKEN_SECRET).parseClaimsJws(token);
            // 토큰 만료 기간 확인
            if(claims.getBody().getExpiration().before(new Date())) {
                log.info("token expired");
                return false;
            }
            return true;
        } catch (JwtException e) {
            throw e;
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
