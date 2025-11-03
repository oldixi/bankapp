package ru.yandex.notifications;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.bind.annotation.*;

@SpringBootTest
@AutoConfigureMessageVerifier
@ActiveProfiles("test")
@TestPropertySource(properties = {"spring.cloud.discovery.enabled=false"})
public abstract class NotificationsBaseTest {
    @BeforeEach
    public void setup() {
        RestAssuredMockMvc.standaloneSetup(new MockNotificationsController());
    }

    @RestController
    @RequestMapping("/api/notifications")
    static class MockNotificationsController {

        @PostMapping("/{email}/mail")
        public void sendNotification(@PathVariable String email,
                                     @RequestParam String text) {

            if (email == null || email.isBlank()) {
                throw new IllegalArgumentException("Email cannot be null or empty");
            }

            if (text == null || text.isBlank()) {
                throw new IllegalArgumentException("Message text cannot be null or empty");
            }

            if (!isValidEmail(email)) {
                throw new IllegalArgumentException("Invalid email format: " + email);
            }

            // Успешная отправка - возвращаем 200 OK без тела
        }

        private boolean isValidEmail(String email) {
            // Простая валидация email для тестов
            return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
        }
    }
}
