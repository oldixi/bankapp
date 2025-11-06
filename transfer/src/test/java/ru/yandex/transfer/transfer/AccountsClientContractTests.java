package ru.yandex.transfer.transfer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.accounts.dto.AccountDto;
import ru.yandex.api.AccountsClient;
import ru.yandex.transfer.config.TestFeignConfig;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment/*.RANDOM_PORT*/.MOCK)
@ActiveProfiles("test")
@Import(TestFeignConfig.class)
@AutoConfigureStubRunner(ids = {"ru.yandex:accounts:0.0.1-SNAPSHOT:stubs:8082",
        "ru.yandex:notifications:0.0.1-SNAPSHOT:stubs:8085"},///*:8088*/
        stubsMode = StubRunnerProperties.StubsMode.LOCAL)
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
}
