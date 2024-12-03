package com.example1.springcloudgateway.jwt;

import com.example1.springcloudgateway.domain.UserInfo;
import com.example1.springcloudgateway.domain.UserRepository;
import com.example1.springcloudgateway.user.dto.UserResponseAllDto;
import com.example1.springcloudgateway.user.service.UserApiService;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenProvider {

    private final UserApiService userService;
    private final UserRepository userRepository;

    @Value("${token.secret}")
    private String TOKEN_SECRET;

    @Value("${token.expiration_time}")
    private String TOKEN_EXPIRATION_TIME;

    @Value("${token.refresh_time}")
    private String TOKEN_REFRESH_TIME;

    final String TOKEN_ACCESS_KEY = "access-token";
    final String TOKEN_REFRESH_KEY = "refresh-token";
    final String TOKEN_CLAIM_USER_NAME = "userName";

    public void createTokenAndAddHeader(HttpServletResponse response, Authentication authResult) throws IOException {
        // 로그인 성공 후 토큰 처리
        String userId = authResult.getName();
        String userName = "";
        String email = "";

        try {
            UserResponseAllDto user = userService.findByUserId(userId);
            userName = user.getUserName();
            email = user.getEmail();
        }catch (Exception e) {
            log.error("Exception : {}", e);
        }


        // JWT Access 토큰 생성
        String accessToken = createAccessToken(userId, userName, email);
        log.info("access token : {}", accessToken);

        String refreshToken = createRefreshToken();
        userService.updateRefreshToken(userId, refreshToken);

        // 기존 헤더에 추가하려는 것과 같은 키값이 없을 때에는 addHeader()나 setHeader()나 동일하게 동작한다.
        response.addHeader(TOKEN_ACCESS_KEY, accessToken);
        response.addHeader(TOKEN_REFRESH_KEY, refreshToken);
    }


    public String createAccessToken(String userId, String userName, String email) {
        log.info("user id : {}", userId);
        log.info("userName : {}", userName);
        log.info("email : {}", email);
        return Jwts.builder()
                .setSubject(userId)
                .claim(TOKEN_CLAIM_USER_NAME, userName)
                // 토큰 유효기간
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(TOKEN_EXPIRATION_TIME)))
                // sign key를 지정(jwt 생성 라이브러리인 jjwt가 지정된 key에 허용된 가장 안전한 알고리즘을 결정하게 해준다.)
                .signWith(SignatureAlgorithm.HS512, TOKEN_SECRET)
                .compact();
    }

    /**
     * AuthenticationFilter.doFilter 메소드에서 UsernamePasswordAuthenticationToken 정보를 세팅할 때 호출된다.
     *
     * @param token
     * @return
     */
    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(TOKEN_SECRET)
                .parseClaimsJws(token)
                .getBody();
    }

    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(TOKEN_SECRET)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject(); // 토큰에 설정된 userId(소유자) 반환
    }

    /**
     * JWT Refresh Token 생성
     * 중복 로그인을 허용하려면 user domain 에 있는 refresh token 값을 반환하고 없는 경우에만 생성하도록 처리한다.
     *
     * @return refresh token
     */
    public String createRefreshToken() {
        return Jwts.builder()
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(TOKEN_REFRESH_TIME)))
                .signWith(SignatureAlgorithm.HS512, TOKEN_SECRET)
                .compact();
    }

    public String updateRefreshToken(String userId, String updateRefreshToken) {
        UserInfo user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("01"));

        user.updateRefreshToken(updateRefreshToken);

        return user.getRefreshToken();
    }

    /**
     * 토큰을 검증
     * @param token
     * @return
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(TOKEN_SECRET).parseClaimsJws(token);
            log.info("token validate");
            return true;
        } catch (ExpiredJwtException exception) {
            log.info("token expired");
            return false;   // 만료
        } catch (JwtException exception) {
            log.info("token not valid");
            return false;   // 변조
        } catch (Exception e) {
            log.info("not expired exception");
            return false;   // 그 외 token 오류
        }

    }

    /**
     * 토큰으로부터 클레임을 만들고, 이를 통해 User 객체를 생성하여 Authentication 객체를 반환
     * @param token
     * @return
     */
    public Authentication getAuthentication(String token) {
        String username = Jwts.parser().setSigningKey(TOKEN_SECRET).parseClaimsJws(token).getBody().getSubject();
        UserDetails userDetails = userService.loadUserByUsername(username);

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
}
