package com.example1.springcloudgateway.Exception;

import lombok.Builder;

@Builder
public record ErrorResponseStyle(
        String status,
        String message,
        String detailMessage
) {
}
