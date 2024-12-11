package com.example1.springcloudgateway.filter;

import org.springframework.stereotype.Component;

@Component
public class CustomFilterService {

    public String checkURI (String uri){
        String newUri = null;
        if(uri.contains("service1")){
            newUri = "http://localhost:8081/service1/tcDstrctMng?userId=";
        } else if(uri.contains("service2")){
            newUri = "http://localhost:8082/service2/tlMvmneqPass?userId=";
        } else {
            newUri = null;
        }
        return newUri;
    }
}
