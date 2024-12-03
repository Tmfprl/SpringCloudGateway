package com.example1.springcloudgateway.user.dto;

import com.example1.springcloudgateway.domain.UserInfo;
import lombok.Getter;

@Getter
public class UserLoginRequestDto {
    private String userId;
    private String userName;
    private String refreshToken;
    private String userStateCode;
    private String password;

    public UserLoginRequestDto(UserInfo entity) {
        this.userId = entity.getUserId();
        this.userName = entity.getUserName();
        this.refreshToken = entity.getRefreshToken();
        this.userStateCode = entity.getUserStateCode();
        this.password = entity.getUserPassword();
    }
}
