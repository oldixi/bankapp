package ru.yandex.front.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class FallbackController {

    @GetMapping("/fallback/accounts")
    public ResponseEntity<Map<String, String>> accountsFallback() {
        return ResponseEntity.status(503)
                .body(Map.of("message", "Сервис аккаунтов временно недоступен",
                        "error", "SERVICE_UNAVAILABLE"));
    }

    @GetMapping("/fallback/cash")
    public ResponseEntity<Map<String, String>> cashFallback() {
        return ResponseEntity.status(503)
                .body(Map.of("message", "Сервис операций с наличными временно недоступен",
                        "error", "SERVICE_UNAVAILABLE"));
    }

    @GetMapping("/fallback/transfer")
    public ResponseEntity<Map<String, String>> transferFallback() {
        return ResponseEntity.status(503)
                .body(Map.of("message", "Сервис переводов временно недоступен",
                        "error", "SERVICE_UNAVAILABLE"));
    }

    @GetMapping("/fallback/notifications")
    public ResponseEntity<Map<String, String>> notificationsFallback() {
        return ResponseEntity.status(503)
                .body(Map.of("message", "Сервис уведомлений временно недоступен",
                        "error", "SERVICE_UNAVAILABLE"));
    }
}
