package com.example1.springcloudgateway.exception;

import com.example1.springcloudgateway.exception.response.ErrorCode;
import com.example1.springcloudgateway.exception.response.ErrorrResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GrolbalExceptionHandler {

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorrResponse> handleEntityNotFound(MissingRequestHeaderException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.badRequest()
                .body(ErrorrResponse.builder()
                        .status(ErrorCode.USER_NOT_FOUND.getStatus())
                        .code(ErrorCode.USER_NOT_FOUND.getCode())
                        .detailMessage(e.getMessage())
                        .build());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorrResponse> handleBadCredentials(BadCredentialsException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.badRequest()
                .body(ErrorrResponse.builder()
                        .status(ErrorCode.WRONG_PASSWORD.getStatus())
                        .code(ErrorCode.WRONG_PASSWORD.getCode())
                        .detailMessage(ErrorCode.WRONG_PASSWORD.getMessage())
                        .build());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorrResponse> handleBadUserId(MissingServletRequestParameterException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.badRequest()
                .body(ErrorrResponse.builder()
                        .status(ErrorCode.USER_NOT_FOUND.getStatus())
                        .code(ErrorCode.USER_NOT_FOUND.getCode())
                        .detailMessage(ErrorCode.USER_NOT_FOUND.getMessage())
                        .build());
    }
}
