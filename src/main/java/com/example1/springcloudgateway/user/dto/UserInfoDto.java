package com.example1.springcloudgateway.user.dto;

import com.example1.springcloudgateway.domain.UserInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;

@Getter
@NoArgsConstructor
@ToString
public class UserInfoDto {
    private String userId;
    private String userName;
    private String password;
    private String email;
    private String userStateCode;
    private LocalDateTime createTime;

    @Builder
    public UserInfoDto(String userId, String userName, String password, String email, String userStateCode, LocalDateTime createTime) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.userStateCode = userStateCode;
        this.createTime = createTime;
    }

    public UserInfo toEntity(BCryptPasswordEncoder encoder) throws Exception {
        return UserInfo.builder()
                .userName(userName)
                .userId(userId)
                .email(email)
                .userPassword(encoder.encode(password)) // 패스워드 인코딩
                .userStateCode(userStateCode)
                .createTime(now())
                .build();
    }
}
