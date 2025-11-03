package ru.yandex.front;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.yandex.accounts.dto.NewAccountDto;
import ru.yandex.front.api.AccountsClient;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureStubRunner(ids = "ru.yandex:accounts:+:stubs:8082",
        stubsMode = StubRunnerProperties.StubsMode.LOCAL,
        repositoryRoot = "file://${project.rootDir}/stubs-repo")
public class AccountsClientContractTests {
    @Autowired
    private AccountsClient accountsClient;

    @Test
    public void shouldLoadUserByUsernameSuccessfully() {
        // When
        var result = accountsClient.loadUserByUsername("existinguser");

        // Then
        assertNotNull(result);
        assertEquals("existinguser", result.getUsername());
        assertTrue(result.isEnabled());
        assertTrue(result.isAccountNonExpired());
    }

    @Test
    public void shouldHandleUserNotFoundForAuth() {
        // When & Then
        assertThrows(UsernameNotFoundException.class, () -> {
            accountsClient.loadUserByUsername("nonexistentuser");
        });
    }

    @Test
    public void shouldRegisterSuccessfully() {
        // Given
        var newAccount = new NewAccountDto();
        newAccount.setName("Петр Петров");
        newAccount.setLogin("newuser");
        newAccount.setPassword("password123");
        newAccount.setConfirmPassword("password123");
        newAccount.setEmail("newuser@example.com");
        newAccount.setBirthdate("1990-01-01");

        // When
        var result = accountsClient.register(newAccount);

        // Then
        assertNotNull(result);
        assertEquals("newuser", result.getLogin());
        assertEquals("Петр Петров", result.getName());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    public void shouldHandlePasswordMismatch() {
        // Given
        var newAccount = new NewAccountDto();
        newAccount.setName("Петр Петров");
        newAccount.setLogin("newuser");
        newAccount.setPassword("password123");
        newAccount.setConfirmPassword("different");
        newAccount.setEmail("newuser@example.com");
        newAccount.setBirthdate("1990-01-01");

        // When
        var result = accountsClient.register(newAccount);

        // Then
        assertNotNull(result);
        assertEquals("newuser", result.getLogin());
        assertFalse(result.getErrors().isEmpty());
        assertEquals("Пароли не совпадают", result.getErrors().get(0));
    }

    @Test
    public void shouldGetAccountSuccessfully() {
        // When
        var result = accountsClient.getAccount("existinguser");

        // Then
        assertNotNull(result);
        assertEquals("existinguser", result.getLogin());
        assertEquals(1500.0, result.getBalance());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    public void shouldUpdatePasswordSuccessfully() {
        // When
        var result = accountsClient.updateAccountPassword("existinguser", "newpass", "newpass");

        // Then
        assertNotNull(result);
        assertEquals("existinguser", result.getLogin());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    public void shouldUpdateProfileSuccessfully() {
        // When
        var result = accountsClient.updateAccount("existinguser", "Новое Имя", "new@example.com", "1985-05-15");

        // Then
        assertNotNull(result);
        assertEquals("existinguser", result.getLogin());
        assertEquals("Новое Имя", result.getName());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    public void shouldDeleteAccountWithZeroBalance() {
        // When
        var result = accountsClient.deleteAccount("userzerobalance");

        // Then
        assertNotNull(result);
        assertEquals("userzerobalance", result.getLogin());
        assertEquals(0.0, result.getBalance());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    public void shouldGetAccountsForTransfer() {
        // When
        var result = accountsClient.getAccounts("existinguser");

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(3, result.size());
    }
}
