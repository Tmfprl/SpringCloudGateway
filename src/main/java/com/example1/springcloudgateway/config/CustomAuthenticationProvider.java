package com.example1.springcloudgateway.config;

import com.example1.springcloudgateway.user.service.UserApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {
    private final UserApiService userDetailsService;
    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    private final UserApiService userApiService;

    /** 인증 정보 생성
     *
     *  유저 정보를 담고 있는 authentication에서 username과 password를 받아와
     *  UserDetail에 있는 유정정보 dto와 비교후 유저인증토큰을 반환한다.
     *
     * @param authentication
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        System.out.println(username);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        // spring security의 규정인지 바로 인코딩을 적용 할 수 없다.
        if (!bCryptPasswordEncoder.matches(password, userDetails.getPassword())) {
            // 로그인 실패 후처리
            userApiService.loginCallback(username, false, "");
            throw new BadCredentialsException("Password Not Match");
        } else {
            // 로그인 성공 후처리
//            userApiService.loginCallback(username, true, "");
            // password에 null이 들어갈 수 없다.
            // 인증된 객체를 생성
            return new UsernamePasswordAuthenticationToken(username, password, null);
        }
    }

    /**
     * AuthenticationProvider 가 어떤 종류의 Authentication 인터페이스를 지원할 지 결정
     * 이는 authenticate() 메서드의 매개 변수로 어떤 형식이 제공될지에 따라서 달라짐
     *
     * AuthenticationFilter 수준에서 아무것도 맞춤 구성하지 않으면 UsernamePasswordAuthenticationToken 클래스가 형식을 정의함
     */
    @Override
    public boolean supports(Class<?> authentication) {
        // UsernamePasswordAuthenticationToken 는 Authentication 인터페이스의 한 구현이며,
        // 사용자 이름과 암호를 이용하는 표준 인증 요청을 나타냄
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
