package ru.yandex.cash.cash;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.accounts.dto.AccountDto;
import ru.yandex.cash.CashService;
import ru.yandex.cash.EAction;
import ru.yandex.api.AccountsClient;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class CashServiceTest {
    @Mock
    private AccountsClient accountsClient;

    @InjectMocks
    private CashService cashService;

    @BeforeEach
    void setUp() {
        cashService = new CashService(accountsClient);
    }

    @Test
    void depositSuccess_ShouldIncreaseBalance() {
        String login = "testuser";
        Double amount = 1000.0;
        Double initialBalance = 24000.0;

        AccountDto account = AccountDto.builder()
                .name("Вася Пупкин")
                .login(login)
                .password("*********")
                .email("test@yandex.ru")
                .birthdate(LocalDate.of(1984,3,31))
                .balance(initialBalance)
                .build();

        when(accountsClient.getAccount(login)).thenReturn(account);
        when(accountsClient.updateBalance(login, initialBalance + amount)).thenReturn(account.toBuilder().balance(initialBalance + amount).build());

        AccountDto result = cashService.actionWithBalance(login, amount, EAction.PUT);

        assertNotNull(result);
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void depositNegativeAmount_ShouldReturnError() {
        String login = "testuser";
        Double amount = -1000.0;
        Double initialBalance = 15100.0;

        AccountDto account = AccountDto.builder()
                .login(login)
                .balance(initialBalance)
                .build();

        when(accountsClient.getAccount(login)).thenReturn(account);

        AccountDto result = cashService.actionWithBalance(login, amount, EAction.PUT);
        System.out.println("result: " + result);

        assertNotNull(result);
        assertEquals(1, result.getErrors().size());
        assertEquals("Сумма перевода должна быть положительной", result.getErrors().get(0));
    }

    @Test
    void withdrawSuccess_ShouldDecreaseBalance() {
        String login = "testuser";
        Double amount = 5000.0;
        Double initialBalance = 25000.0;

        AccountDto account = AccountDto.builder()
                .name("Вася Пупкин")
                .login(login)
                .password("*********")
                .email("test@yandex.ru")
                .birthdate(LocalDate.of(1984,3,31))
                .balance(initialBalance)
                .build();

        when(accountsClient.getAccount(login)).thenReturn(account);
        when(accountsClient.updateBalance(login, initialBalance - amount)).thenReturn(account.toBuilder().balance(initialBalance - amount).build());

        AccountDto result = cashService.actionWithBalance(login, amount, EAction.GET);

        assertNotNull(result);
        assertEquals(20000.0, result.getBalance());
        assertTrue(result.getErrors().isEmpty());
        verify(accountsClient).updateBalance(login, initialBalance - amount);
    }

    @Test
    void withdrawInsufficientFunds_ShouldReturnError() {
        String login = "testuser";
        Double amount = 4000.0;
        Double initialBalance = 100.0;

        AccountDto account = AccountDto.builder()
                .login(login)
                .balance(initialBalance)
                .build();

        when(accountsClient.getAccount(login)).thenReturn(account);

        AccountDto result = cashService.actionWithBalance(login, amount, EAction.GET);

        assertNotNull(result);
        assertEquals(1, result.getErrors().size());
        assertEquals("Недостаточно средств на счете", result.getErrors().get(0));
    }

    @Test
    void invalidAction_ShouldReturnError() {
        String login = "testuser";
        Double amount = 100.0;
        Double initialBalance = 15100.0;

        AccountDto account = AccountDto.builder()
                .login(login)
                .balance(initialBalance)
                .build();

        when(accountsClient.getAccount(login)).thenReturn(account);

        AccountDto result = cashService.actionWithBalance(login, amount, EAction.DUMMY);

        assertNotNull(result);
        assertEquals(1, result.getErrors().size());
        assertEquals("Тип операции не поддерживается", result.getErrors().get(0));
    }

    @Test
    void withdrawZeroAmount_ShouldReturnError() {
        String login = "testuser";
        Double amount = 0.0;
        Double initialBalance = 15100.0;

        AccountDto account = AccountDto.builder()
                .login(login)
                .balance(initialBalance)
                .build();

        when(accountsClient.getAccount(login)).thenReturn(account);

        AccountDto result = cashService.actionWithBalance(login, amount, EAction.GET);

        assertNotNull(result);
        assertEquals(1, result.getErrors().size());
        assertEquals("Сумма перевода должна быть положительной", result.getErrors().get(0));
    }
}
