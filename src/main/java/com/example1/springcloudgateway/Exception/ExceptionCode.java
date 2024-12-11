package com.example1.springcloudgateway.Exception;

import lombok.Builder;
import lombok.Getter;

@Getter
public enum ExceptionCode {

    NOT_FOUND_URL("404", "NOTFOUND_001", "Could not find URL"),;

    private final String status;
    private final String code;
    private final String message;

    ExceptionCode(String status, String message, String code) {
        this.status = status;
        this.message = message;
        this.code = code;
    }
}
