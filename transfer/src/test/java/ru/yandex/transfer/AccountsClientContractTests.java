package ru.yandex.transfer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import ru.yandex.accounts.dto.AccountDto;
import ru.yandex.transfer.api.AccountsClient;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureStubRunner(
        ids = "ru.yandex:transfer:+:stubs:8084",
        stubsMode = StubRunnerProperties.StubsMode.LOCAL,
        repositoryRoot = "file://${project.rootDir}/stubs-repo"
)
public class AccountsClientContractTests {
    @Autowired
    private AccountsClient accountsClient;

    @Test
    public void shouldGetAccountSuccessfully() {
        AccountDto result = accountsClient.getAccount("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getLogin());
        assertEquals(1500.0, result.getBalance());
        assertEquals("testuser@example.com", result.getEmail());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    public void shouldUpdateBalanceSuccessfully() {
        AccountDto result = accountsClient.updateBalance("testuser", 2500.0);

        assertNotNull(result);
        assertEquals("testuser", result.getLogin());
        assertEquals(2500.0, result.getBalance());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    public void shouldHandleAccountNotFound() {
        assertThrows(feign.FeignException.NotFound.class, () -> {
            accountsClient.getAccount("nonexistentuser");
        });
    }

    @Test
    public void shouldHandleUpdateBalanceForNewUser() {
        AccountDto result = accountsClient.updateBalance("newuser", 0.0);

        assertNotNull(result);
        assertEquals("newuser", result.getLogin());
        assertEquals(0.0, result.getBalance());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    public void shouldHandleNegativeBalance() {
        AccountDto result = accountsClient.updateBalance("testuser", -500.0);

        assertNotNull(result);
        assertEquals("testuser", result.getLogin());
        assertEquals(-500.0, result.getBalance());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    public void shouldHandleServiceErrorForGetAccount() {
        assertThrows(Exception.class, () -> {
            accountsClient.getAccount("erroruser");
        });
    }

    @Test
    public void shouldHandleServiceErrorForUpdateBalance() {
        assertThrows(Exception.class, () -> {
            accountsClient.updateBalance("erroruser", 1000.0);
        });
    }

    @Test
    public void shouldGetRichUserAccount() {
        AccountDto result = accountsClient.getAccount("richuser");

        assertNotNull(result);
        assertEquals("richuser", result.getLogin());
        assertEquals(10000.0, result.getBalance());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    public void shouldUpdateBalanceForRecipient() {
        AccountDto result = accountsClient.updateBalance("recipientuser", 1500.0);

        assertNotNull(result);
        assertEquals("recipientuser", result.getLogin());
        assertEquals(1500.0, result.getBalance());
        assertTrue(result.getErrors().isEmpty());
    }
}
