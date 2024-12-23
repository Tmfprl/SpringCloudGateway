package com.example1.springcloudgateway.Exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity handleException(CustomException e) {
        log.error(e.getMessage());
        return ResponseEntity.badRequest()
                .body(ErrorResponseStyle.builder()
                        .status(ExceptionCode.NOT_FOUND_URL.getStatus())
                        .message(ExceptionCode.NOT_FOUND_URL.getMessage())
                        .detailMessage(ExceptionCode.NOT_FOUND_URL.getCode())
                        .build());
    }

}
