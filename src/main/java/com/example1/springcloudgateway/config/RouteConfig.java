package com.example1.springcloudgateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {
    //bean 등록
    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r-> r.path("/api/login/**")
                    .uri("http://localhost:8083"))
                .route(r -> r.path("service1/**")
                        .filters(f -> f.addResponseHeader("Authentication-check", "true"))
                        .uri("http://localhost:8081/service1/tcDstrctMng"))
                .build();
    }
}
