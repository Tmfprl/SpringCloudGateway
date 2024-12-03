package com.example1.springcloudgateway.exception.response;

import lombok.Builder;

@Builder
public record ErrorrResponse (
        String status,
        String code,
        String detailMessage
){
}
