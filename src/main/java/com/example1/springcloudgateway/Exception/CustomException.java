package com.example1.springcloudgateway.Exception;

public class CustomException extends RuntimeException {
    private final ExceptionCode errorCode;

    public CustomException(ExceptionCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

}
