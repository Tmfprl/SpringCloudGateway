package com.example1.springcloudgateway.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

@Slf4j
@Entity @Table(name="gyeun_user_info", schema = "srlk")
@ToString @Getter
@DynamicInsert @DynamicUpdate
@NoArgsConstructor
public class UserInfo implements UserDetails {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "user_no")
    private Long userNo;

    @Column(name = "user_password", length = 100)
    private String userPassword;

    @Column(name = "user_name", length = 50)
    private String userName;

    @Column(name = "user_state_code", columnDefinition = "varchar(20) default '00'")
    private String userStateCode;

    @Column(name = "email_addr", length = 100)
    private String emailAddr;

    @Column(name = "last_login_date")
    private LocalDateTime lastLoginDate;

    @Column(name = "login_fail_count", columnDefinition = "smallint default 0")
    private Integer loginFailCount;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "user_id", unique = true)
    private String userId;


    @Column(name = "creates_date")
    private LocalDateTime createTime;

    @Builder
    public UserInfo (Long userNo, String userId, String userName, String email,
                     String userPassword, String userStateCode, LocalDateTime createTime, LocalDateTime lastLoginDate, Integer loginFailCount, String refreshToken) {
        this.userNo = userNo;
        this.userId = userId;
        this.userName = userName;
        this.emailAddr = email;
        this.userPassword = userPassword;
        this.userStateCode = userStateCode;
        this.createTime = createTime;
        this.lastLoginDate = lastLoginDate;
    }

    /**
     * 사용자 refresh token 정보를 필드에 입력한다.
     *
     * @param refreshToken
     * @return
     */
    public UserInfo updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }

    /**
     * 로그인 실패 시 로그인실패수를 증가시키고 5회 이상 실패한 경우 회원상태를 정지로 변경
     *
     * @return User 사용자 엔티티
     */
    public void failLogin() {
        this.loginFailCount = loginFailCount + 1;
        if (this.loginFailCount >= 5) {
            this.userStateCode = UserStateCode.HALT.getKey();
        } else {
            System.out.println("login fail count : "+loginFailCount);
        }
    }

    /**
     * 로그인 성공 시 로그인실패수와 마지막로그인일시 정보를 갱신
     *
     * @return User 사용자 엔티티
     */
    public UserInfo successLogin() {
        log.info("login success");
        this.loginFailCount = 0;
        this.lastLoginDate = LocalDateTime.now();
        return this;
    }

    public String getStateKey(String userStateCode) {
        UserStateCode usc = UserStateCode.findByKey(userStateCode);
        return usc.getKey();
    }

    // user 권한 정보 (사용 안함)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return userPassword;
    }

    @Override
    public String getUsername() {
        return userId;
    }

    // getter
    public String getUserName() {
        return userName;
    }
}
