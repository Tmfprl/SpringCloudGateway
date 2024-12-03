package com.example1.springcloudgateway.user.service;

import com.example1.springcloudgateway.jwt.TokenProvider;
import com.example1.springcloudgateway.user.dto.UserResponseAllDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomAuthSuccessHandler implements AuthenticationSuccessHandler {

    private final UserApiService userApiService;
    private final TokenProvider tokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        UserDetails userDetails = userApiService.loadUserByUsername(authentication.getName().toString());
        String userId = authentication.getName();

        try {
            UserResponseAllDto user = userApiService.findByUserId(userId);
            String jwtToken = tokenProvider.createAccessToken(authentication.getName(), user.getUserName(), user.getEmail());
            if(!userId.isEmpty()) {
                log.info("userId : {}, user.getUserId : {}", userId, user.getUserId());
                response.addHeader("token", jwtToken);
                response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken);
                response.setContentType("application/json");
                response.getWriter().write("{\"accessToken\": \"" + jwtToken + "\", \"message\": \"Login successful\"}");

            } else {
                log.info("login failed");
                response.sendRedirect("/login");
            }
        } catch (Exception e) {
        throw new RuntimeException(e);
    }
    }
}





