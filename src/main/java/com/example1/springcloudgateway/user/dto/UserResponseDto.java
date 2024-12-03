package com.example1.springcloudgateway.user.dto;

import com.example1.springcloudgateway.domain.UserInfo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@ToString
public class UserResponseDto {


    private String userId;
    private String userName;
    private String email;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime lastLoginDate;

    @Builder
    public UserResponseDto(String userId, String userName, String email, String userStateCode, String userStateName, LocalDateTime lastLoginDate) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.lastLoginDate = lastLoginDate;
    }

    public Page<UserResponseDto> toPage (Page<UserInfo> entity) {
        Page<UserResponseDto> dto = entity.map(m -> toDto(m));
        return dto;
    }

    public static UserResponseDto toDto (UserInfo entity) {
        return UserResponseDto.builder()
                .userId(entity.getUserId())
                .userName(entity.getUserName())
                .email(entity.getEmailAddr())
                .lastLoginDate(entity.getLastLoginDate())
                .build();
    }
}
