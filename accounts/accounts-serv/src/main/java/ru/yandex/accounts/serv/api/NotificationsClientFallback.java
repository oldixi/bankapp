package ru.yandex.accounts.serv.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Component
public class NotificationsClientFallback implements NotificationsClient {
    @Override
    public void sendNotification(@PathVariable String email, @RequestParam String text) {
        log.error("Fallback: Unable to send notification for email: {}", email);
    }
}
