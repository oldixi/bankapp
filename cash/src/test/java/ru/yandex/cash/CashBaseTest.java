package ru.yandex.cash;

import feign.FeignException;
import feign.Request;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.context.WebApplicationContext;
import ru.yandex.accounts.dto.AccountDto;
import ru.yandex.cash.api.AccountsClient;
import ru.yandex.cash.cash.CashController;

import java.time.LocalDate;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMessageVerifier
@ActiveProfiles("test")
public abstract class CashBaseTest {
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private CashController cashController;
    @MockitoBean
    private AccountsClient accountsClient;

    @BeforeEach
    public void setup() {
        RestAssuredMockMvc.standaloneSetup(cashController);
        initMocks();
    }

    private void initMocks() {
        when(accountsClient.getAccount("testuser"))
                .thenReturn(AccountDto.builder()
                        .name("Иван Иванов")
                        .login("testuser")
                        .password("encrypted")
                        .email("test@yandex.ru")
                        .birthdate(LocalDate.of(1984, 3, 31))
                        .balance(1500.0)
                        .build());

        when(accountsClient.updateBalance(eq("testuser"), anyDouble()))
                .thenAnswer(invocation -> {
                    Double newBalance = invocation.getArgument(1);
                    return AccountDto.builder()
                            .name("Иван Иванов")
                            .login("testuser")
                            .password("encrypted")
                            .email("test@yandex.ru")
                            .birthdate(LocalDate.of(1984, 3, 31))
                            .balance(newBalance)
                            .build();
                });
    }

    protected void setupUserWithBalance(String login, Double balance) {
        when(accountsClient.getAccount(login))
                .thenReturn(AccountDto.builder()
                        .login(login)
                        .balance(balance)
                        .build());
    }

    protected void setupUserNotFound(String login) {
        when(accountsClient.getAccount(login))
                .thenThrow(new FeignException.NotFound("Аккаунт не найден",
                        Request.create(Request.HttpMethod.GET, "/api/accounts/" + login,
                                Map.of(), null, null, null), null, null));
    }
}
