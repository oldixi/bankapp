package ru.yandex.accounts.serv.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class FallbackController {
    @GetMapping("/fallback/notifications")
    public ResponseEntity<Map<String, String>> notificationsFallback() {
        return ResponseEntity.status(503)
                .body(Map.of("message", "Сервис уведомлений временно недоступен",
                        "error", "SERVICE_UNAVAILABLE"));
    }
}
