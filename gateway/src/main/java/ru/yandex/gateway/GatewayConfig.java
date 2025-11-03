package ru.yandex.gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("front", r -> r
                        .path("/**", "/", "/login", "/logout", "/signup")
                        .uri("lb://front"))
                .route("accounts", r -> r
                        .path("/api/accounts/**")
                        .filters(GatewayFilterSpec::tokenRelay)
                        .uri("lb://accounts"))
                .route("cash", r -> r
                        .path("/api/cash/**")
                        .filters(GatewayFilterSpec::tokenRelay)
                        .uri("lb://cash"))
                .route("transfer", r -> r
                        .path("/api/transfer/**")
                        .filters(GatewayFilterSpec::tokenRelay)
                        .uri("lb://transfer"))
                .route("notifications", r -> r
                        .path("/api/notifications/**")
                        .filters(GatewayFilterSpec::tokenRelay)
                        .uri("lb://notifications"))
                .build();
    }
}
