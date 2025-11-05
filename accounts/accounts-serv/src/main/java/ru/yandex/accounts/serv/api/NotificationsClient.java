package ru.yandex.accounts.serv.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "notifications", fallback = NotificationsClient.NotificationsFallback.class)
public interface NotificationsClient {
    @PostMapping("/api/notifications/{email}/email")
    void sendNotification(@PathVariable String email, @RequestParam String message);

    @Component
    class NotificationsFallback implements NotificationsClient {
        @Override
        public void sendNotification(@PathVariable String email, @RequestParam String message) {
            System.err.println("Сервис отправки сообщений временно недоступен. Невозможно отправить сообщение на адрес " + email);
        }
    }
}
