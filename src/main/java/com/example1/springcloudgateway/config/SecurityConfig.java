package com.example1.springcloudgateway.config;

import com.example1.springcloudgateway.filter.AuthenticationFilter;
import com.example1.springcloudgateway.jwt.TokenProvider;
import com.example1.springcloudgateway.user.service.CustomAuthSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@RequiredArgsConstructor
// Spring Security 설정들을 활성화시켜 준다
@EnableWebSecurity (debug = true) // request가 올 떄마다 어떤 filter를 사용하고 있는지 출력을 해준다.
@EnableMethodSecurity
public class SecurityConfig {
    private final TokenProvider tokenProvider;
    private final CustomAuthenticationProvider cutomAuthenticationProvider;

    @Value("${token.secret}")
    private String TOKEN_SECRET;

    // 토큰 인증 필터가 추가 되어야한다.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomAuthSuccessHandler customAuthSuccessHandler) throws Exception {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(cutomAuthenticationProvider, tokenProvider);

        http
                .csrf(csrf -> csrf.disable())
                .authorizeRequests(authorize -> authorize
                        // /auth/signIn 경로에 대한 접근을 허용합니다. 이 경로는 인증 없이 접근할 수 있습니다.
                        .requestMatchers("/user/**", "/error", "/login", "/loginSuccess").permitAll()  // 모든접근허용
                        .requestMatchers("/getUser/**").permitAll()
                        .anyRequest().authenticated()   // 제한된 접근, 그 외의 모든 요청은 인증이 필요합니다.
                )
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(
                        (formLogin) -> formLogin
                                .loginProcessingUrl("/login")
                                .successHandler(customAuthSuccessHandler)
                )
                .logout(
                        (logout) -> logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout")))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

        ;
        // 설정된 보안 필터 체인을 반환합니다.
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers("/favicon.ico");
    }
}

