package ru.yandex.transfer.api;

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
}
