package com.example1.springcloudgateway.exception;

import com.example1.springcloudgateway.exception.response.ErrorCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Getter
public class ServiceCoustomException extends RuntimeException{
    private ErrorCode errorCode;

    public ServiceCoustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ServiceCoustomException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;

    }

    public ServiceCoustomException(ErrorCode errorCode, Throwable throwable) {
        super(throwable.getMessage(), throwable);
        this.errorCode = errorCode;
    }

    public ErrorCode errorCode() {
        return this.errorCode;
    }
}
