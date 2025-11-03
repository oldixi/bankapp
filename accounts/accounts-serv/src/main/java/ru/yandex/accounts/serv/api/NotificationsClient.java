package ru.yandex.accounts.serv.api;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "notifications", configuration = OAuth2FeignConfiguration.class)
public interface NotificationsClient {
    @PostMapping("/api/notifications/{email}/email")
    @CircuitBreaker(name = "notificationsService", fallbackMethod = "sendNotificationFallback")
    @Retry(name = "notificationsService", fallbackMethod = "sendNotificationFallback")
    void sendNotification(@PathVariable String email, @RequestParam String message);

    default void sendNotificationFallback(@RequestParam String email, @RequestParam String message, Throwable throwable) {
        System.err.println("Fallback triggered for email: " + email + ", Error: " + throwable.getMessage());
    }
}
