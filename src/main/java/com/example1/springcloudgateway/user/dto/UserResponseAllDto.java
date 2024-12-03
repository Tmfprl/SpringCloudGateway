package com.example1.springcloudgateway.user.dto;

import com.example1.springcloudgateway.domain.UserInfo;
import lombok.Getter;

@Getter
public class UserResponseAllDto {
    private String userId;
    private String userName;
    private String email;
    private String refreshToken;
    private String userStateCode;
    private Boolean hasPassword;

    /**
     * UserResponseDto는 Entity의 필드 중 일부만 사용하므로 생성자로 Entity를 받아 필드에 값을 넣는다.
     * 굳이 모든 필드를 가진 생성자가 필요하지 않다.
     *
     * @param entity
     */
    public UserResponseAllDto(UserInfo entity){
        this.userId = entity.getUserId();
        this.userName = entity.getUserName();
        this.email = entity.getEmailAddr();
        this.refreshToken = entity.getRefreshToken();
        this.userStateCode = entity.getUserStateCode();
        this.hasPassword = entity.getUserPassword() != null && !"".equals(entity.getUserPassword());
    }
}
