package com.example1.springcloudgateway.filter;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ExtractBody {

    public String extractValueFromBody(String body, String key) {
        if (body == null || key == null) {
            return null;
        }

        // key= 와 &로 구분된 데이터를 찾기 위한 정규식
        String regex = key + "=([^&]*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(body);

        if (matcher.find()) {
            return matcher.group(1); // key에 대응하는 값 반환
        }
        return null; // 값이 없으면 null 반환
    }

    public String extractTokenFromBody(String responseBody, String key) {
        if ((responseBody == null || responseBody.isEmpty()) && (key == null || key.isEmpty())) {
            return null;
        }

        // key= 와 &로 구분된 데이터를 찾기 위한 정규식
        String regex = "\"" + key + "\"\\s*:\\s*\"([^\"]+)\"";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(responseBody);

        if (matcher.find()) {
            return matcher.group(1); // key에 대응하는 값 반환
        }
        return null; // 값이 없으면 null 반환
    }
}
