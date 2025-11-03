package ru.yandex.accounts.serv;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.accounts.serv.api.NotificationsClient;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureStubRunner(ids = "ru.yandex:notifications:+:stubs:8085",
        stubsMode = StubRunnerProperties.StubsMode.LOCAL,
        repositoryRoot = "file://${project.rootDir}/stubs-repo"
)
public class NotificationsClientContractTest {
    @Autowired
    private NotificationsClient notificationsClient;

    @Test
    public void shouldSendWelcomeNotificationSuccessfully() {
        assertDoesNotThrow(() -> {
            notificationsClient.sendNotification("user@example.com",
                    "Добро пожаловать в банковское приложение! Ваш аккаунт успешно создан.");
        });
    }

    @Test
    public void shouldSendBalanceChangeNotificationSuccessfully() {
        assertDoesNotThrow(() -> {
            notificationsClient.sendNotification("user@example.com",
                    "Баланс вашего счета в банковском приложении изменился. Новый баланс: 2500.0");
        });
    }

    @Test
    public void shouldSendPasswordChangeNotificationSuccessfully() {
        assertDoesNotThrow(() -> {
            notificationsClient.sendNotification("user@example.com",
                    "Ваш пароль в банковском приложении успешно изменен");
        });
    }

    @Test
    public void shouldSendAccountDeletionNotificationSuccessfully() {
        assertDoesNotThrow(() -> {
            notificationsClient.sendNotification("user@example.com",
                    "Ваш аккаунт был удален из банковского приложения");
        });
    }

    @Test
    public void shouldSendProfileUpdateNotificationSuccessfully() {
        assertDoesNotThrow(() -> {
            notificationsClient.sendNotification("user@example.com",
                    "Информация о вашем аккаунте в банковском приложении успешно изменена");
        });
    }

    @Test
    public void shouldHandleInvalidEmail() {
        assertThrows(Exception.class, () -> {
            notificationsClient.sendNotification("invalid-email", "Текст уведомления");
        });
    }
}
