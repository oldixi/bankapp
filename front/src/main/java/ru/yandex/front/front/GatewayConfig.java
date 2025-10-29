package ru.yandex.front.front;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("accounts", r -> r
                        .path("/api/accounts/**")
                        .filters(f -> f
                                .tokenRelay()
                                .circuitBreaker(config -> config
                                        .setName("accountsService")
                                        .setFallbackUri("forward:/fallback/accounts")
                                )
                        )
                        .uri("lb://accounts"))
                .route("cash", r -> r
                        .path("/api/cash/**")
                        .filters(f -> f
                                .tokenRelay()
                                .circuitBreaker(config -> config
                                        .setName("cashService")
                                        .setFallbackUri("forward:/fallback/cash")
                                )
                        )
                        .uri("lb://cash"))
                .route("transfer", r -> r
                        .path("/api/transfer/**")
                        .filters(f -> f
                                .tokenRelay()
                                .circuitBreaker(config -> config
                                        .setName("transferService")
                                        .setFallbackUri("forward:/fallback/transfer")
                                )
                        )
                        .uri("lb://transfer"))
                .route("notifications", r -> r
                        .path("/api/notifications/**")
                        .filters(f -> f
                                .tokenRelay()
                                .circuitBreaker(config -> config
                                        .setName("notificationsService")
                                        .setFallbackUri("forward:/fallback/notifications")
                                )
                        )
                        .uri("lb://notifications"))
                .build();
    }
}
