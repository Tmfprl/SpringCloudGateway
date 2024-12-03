package com.example1.springcloudgateway.filter;

import com.example1.springcloudgateway.jwt.TokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final TokenProvider tokenProvider;
    private final CustomAuthenticationProvider cutomAuthenticationProvider;

    public AuthenticationFilter(CustomAuthenticationProvider cutomAuthenticationProvider, TokenProvider tokenProvider) {
        this.cutomAuthenticationProvider = cutomAuthenticationProvider;
        this.tokenProvider = tokenProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(cutomAuthenticationProvider);
        return authenticationManagerBuilder.build();
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        String username = obtainUsername(request);
        String password = obtainPassword(request);

        try {

            username = username.trim();
            log.info("authenticate run user name : {} ", username);

        } catch (RuntimeException e) {
            log.error(e.getLocalizedMessage());
        }

        UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(username, password);

        Authentication auth = cutomAuthenticationProvider.authenticate(upat);

        // 유저 로그인 정보를 받아오 인증정보를 리턴한다.
        return auth;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        log.info("doFilter run");
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (!requiresAuthentication(httpRequest, httpResponse)) {
            chain.doFilter(request, response);
            return;
        }

        Authentication authentication;

        try {
            authentication = attemptAuthentication(httpRequest, httpResponse);
            if(authentication == null){
                return;
            }
            cutomAuthenticationProvider.authenticate(authentication);

        } catch (RuntimeException e) {
            SecurityContextHolder.getContext().setAuthentication(null);
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            log.error("AuthenticationFilter doFilter error: {}", e.getMessage());
        }
        chain.doFilter(request, response);
    }
}
