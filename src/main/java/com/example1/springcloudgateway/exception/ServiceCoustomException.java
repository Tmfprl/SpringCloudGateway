package org.example.web_mng_authentication.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.web_mng_authentication.exception.response.ErrorCode;
import org.example.web_mng_authentication.exception.response.ErrorrResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;

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
