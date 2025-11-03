package ru.yandex.front;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import ru.yandex.front.api.TransferClient;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureStubRunner(ids = "ru.yandex:transfer:+:stubs:8084",
        stubsMode = StubRunnerProperties.StubsMode.LOCAL,
        repositoryRoot = "file://${project.rootDir}/stubs-repo")
public class TransferClientContractTests {
    @Autowired
    private TransferClient transferClient;

    @Test
    public void shouldTransferSuccessfully() {
        // When
        var result = transferClient.transfer("testuser", 500.0, "recipientuser");

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getLogin());
        assertEquals(1000.0, result.getBalance());
        assertEquals("testuser@example.com", result.getEmail());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    public void shouldHandleInsufficientFunds() {
        // When
        var result = transferClient.transfer("testuser", 2000.0, "recipientuser");

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getLogin());
        assertFalse(result.getErrors().isEmpty());
        assertEquals("Недостаточно средств на счете", result.getErrors().get(0));
    }

    @Test
    public void shouldHandleNegativeAmount() {
        // When
        var result = transferClient.transfer("testuser", -100.0, "recipientuser");

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getLogin());
        assertFalse(result.getErrors().isEmpty());
        assertEquals("Сумма перевода должна быть положительной", result.getErrors().get(0));
    }

    @Test
    public void shouldHandleToUserNotFound() {
        // When
        var result = transferClient.transfer("testuser", 100.0, "nonexistentuser");

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getLogin());
        assertFalse(result.getErrors().isEmpty());
        assertEquals("Получатель не найден", result.getErrors().get(0));
    }

    @Test
    public void shouldHandleFromUserNotFound() {
        // When
        var result = transferClient.transfer("nonexistentuser", 100.0, "recipientuser");

        // Then
        assertNotNull(result);
        assertEquals("nonexistentuser", result.getLogin());
        assertFalse(result.getErrors().isEmpty());
        assertTrue(result.getErrors().get(0).contains("404"));
    }

    @Test
    public void shouldHandleZeroBalanceTransfer() {
        // When
        var result = transferClient.transfer("newuser", 100.0, "recipientuser");

        // Then
        assertNotNull(result);
        assertEquals("newuser", result.getLogin());
        assertFalse(result.getErrors().isEmpty());
        assertEquals("Недостаточно средств на счете", result.getErrors().get(0));
    }
}
