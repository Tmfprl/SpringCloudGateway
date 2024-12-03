package org.example.web_mng_authentication.exception.response;

import lombok.Builder;

@Builder
public record ErrorrResponse (
        String status,
        String code,
        String detailMessage
){
}
