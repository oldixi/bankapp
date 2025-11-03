package ru.yandex.front;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import ru.yandex.front.api.CashClient;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureStubRunner(ids = "ru.yandex:cash:+:stubs:8083",
        stubsMode = StubRunnerProperties.StubsMode.LOCAL,
        repositoryRoot = "file://${project.rootDir}/stubs-repo")
public class CashClientContractTests {
    @Autowired
    private CashClient cashClient;

    @Test
    public void shouldDepositMoneyWithPut() {
        var result = cashClient.actionWithBalance("testuser", 1000.0, "PUT");

        assertNotNull(result);
        assertEquals("testuser", result.getLogin());
        assertEquals(2500.0, result.getBalance());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    public void shouldWithdrawMoneyWithGet() {
        var result = cashClient.actionWithBalance("testuser", 500.0, "GET");

        assertNotNull(result);
        assertEquals("testuser", result.getLogin());
        assertEquals(2000.0, result.getBalance());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    public void shouldHandleWithdrawInsufficientFunds() {
        var result = cashClient.actionWithBalance("testuser", 5000.0, "GET");

        assertNotNull(result);
        assertEquals("testuser", result.getLogin());
        assertEquals(1500.0, result.getBalance());
        assertFalse(result.getErrors().isEmpty());
        assertEquals("Недостаточно средств на счете", result.getErrors().get(0));
    }

    @Test
    public void shouldHandleInvalidAction() {
        var result = cashClient.actionWithBalance("testuser", 100.0, "INVALID");

        assertNotNull(result);
        assertEquals("testuser", result.getLogin());
        assertEquals(1500.0, result.getBalance());
        assertFalse(result.getErrors().isEmpty());
        assertEquals("Тип операции не поддерживается", result.getErrors().get(0));
    }

    @Test
    public void shouldHandleNegativeAmountForPut() {
        var result = cashClient.actionWithBalance("testuser", -100.0, "PUT");

        assertNotNull(result);
        assertEquals("testuser", result.getLogin());
        assertEquals(1500.0, result.getBalance());
        assertFalse(result.getErrors().isEmpty());
        assertEquals("Сумма перевода должна быть положительной", result.getErrors().get(0));
    }

    @Test
    public void shouldHandleZeroAmountForGet() {
        var result = cashClient.actionWithBalance("testuser", 0.0, "GET");

        assertNotNull(result);
        assertEquals("testuser", result.getLogin());
        assertEquals(1500.0, result.getBalance());
        assertFalse(result.getErrors().isEmpty());
        assertEquals("Сумма перевода должна быть положительной", result.getErrors().get(0));
    }
}