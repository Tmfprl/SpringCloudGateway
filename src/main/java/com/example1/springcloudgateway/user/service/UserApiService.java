package com.example1.springcloudgateway.user.service;

import com.example1.springcloudgateway.domain.UserInfo;
import com.example1.springcloudgateway.domain.UserRepository;
import com.example1.springcloudgateway.user.dto.UserInfoDto;
import com.example1.springcloudgateway.user.dto.UserResponseAllDto;
import com.example1.springcloudgateway.user.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
// UserDetailsService_spring security에서 유저의 정보를 가져오는 인테페이스이다. (유저정보를 불러와서 UserDe)
public class UserApiService implements UserDetailsService {

    final UserRepository userRepository;
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // refresh token 재발급
    @Transactional
    public void updateRefreshToken(String userId, String updateRefreshToken) {
        UserInfo user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("01"));

        user.updateRefreshToken(updateRefreshToken);

        log.info("refresh token : ",updateRefreshToken);
    }

    // 로그인 후처리 (로그인 성공, 실패 처리)
    @Transactional
    public void loginCallback(String userId, Boolean successAt, String failContent) {
        Optional<UserInfo> result = userRepository.findByUserId(userId);

        System.out.println("login "+ successAt);

        // Optional 객체가 값을 가지고 있다면 true, 값이 없다면 false 리턴
        if(result.isPresent()) {
            UserInfo user = result.get();
//            log.info("login request User information {}", user);
            if (Boolean.TRUE.equals(successAt)) {
                user.successLogin();
            } else {
                log.info("login fail");
                user.failLogin();
                log.error(user.getLoginFailCount().toString());
            }
        }
    }

    public UserDetails userDetails(String username) throws UsernameNotFoundException {
        UserInfo userInfo = userRepository.findByUserId(username).orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
        log.info("founding user info......");
        return User.builder()
                .username(userInfo.getUserId())
                .build();
    }

    // user 정보를 entity로 받아 user로 반환해준다. UserDetails_spring security에서 사용자의 정보를 담는 인터페이스 이다.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserInfo userInfo = userRepository.findByUserId(username).orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
        log.info("Success find user {}", username);
        log.info(userInfo.toString());
        return User.builder()
                .username(userInfo.getUserId())
                .password(userInfo.getUserPassword())
                .build();
    }

    public Long signIn(UserInfoDto userInfoDto) throws Exception {
        return userRepository.save(userInfoDto.toEntity(passwordEncoder)).getUserNo();
    }

    public Page<UserResponseDto> findById (String userId, Pageable pageable) throws Exception {
        Page<UserInfo> entity = userRepository.findByUserId(userId, pageable);
        Page<UserResponseDto> dto = new UserResponseDto().toPage(entity);
        return dto;
    }

    public UserResponseAllDto findByUserId(String userId) throws Exception {
        UserInfo entity = userRepository.findByUserId(userId).orElseThrow(() -> new UsernameNotFoundException("01"));
        return new UserResponseAllDto(entity);
    }

}
