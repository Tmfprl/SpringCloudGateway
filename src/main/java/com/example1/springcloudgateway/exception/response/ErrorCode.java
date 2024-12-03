package org.example.web_mng_authentication.exception.response;

import lombok.Getter;

@Getter
public enum ErrorCode {

    //    HTTP 상태 코드 표 : https://hongong.hanbit.co.kr/http-%EC%83%81%ED%83%9C-%EC%BD%94%EB%93%9C-%ED%91%9C-1xx-5xx-%EC%A0%84%EC%B2%B4-%EC%9A%94%EC%95%BD-%EC%A0%95%EB%A6%AC/

    USER_NOT_FOUND("404", "USER_001", "not found user"),
    WRONG_INPUT("400", "INPUT_ERROR", "wrong input"),
    SERVER_ERROR("500", "SERVER_ERROR", "server error"),
    MISSING_HEADER("400", "BAD_REQUEST", "not found header"),
    WRONG_PASSWORD("404", "USER_002", "password not match"),
    TOKEN_EXPIRED("401", "TOKEN_EXPIRED", "token expired"),
    ;

    private final String status;
    private final String message;
    private final String code;

    ErrorCode(String status, String message, String code) {
        this.status = status;
        this.message = message;
        this.code = code;
    }

}
