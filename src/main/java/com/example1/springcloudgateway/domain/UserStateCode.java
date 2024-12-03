package com.example1.springcloudgateway.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum UserStateCode {

    WAIT("00", "대기"),
    NORMAL("01", "정상"),
    HALT("07", "정지"),
    LEAVE("08", "탈퇴"),
    DELETE("09", "삭제");

    private final String key;
    private final String title;

    /**
     * 사용자 상태 코드로 상수 검색
     *
     * @param key 사용자 상태 코드
     * @return UserStateCode 사용자 상태 코드 상수
     */
    public static UserStateCode findByKey(String key) {     // 키가 상태 코드이고
        // 배열 안에 값을 filter 의 조건에 따라 집어 넣는다.
        // findAny() 는 조건에 일치하는 값을 하나 넣는 것으로 순서에 상관 없이 일치하는 값을 찾는다.
        return Arrays.stream(UserStateCode.values()).filter(c -> c.getKey().equals(key)).findAny().orElse(null);
    }

}
