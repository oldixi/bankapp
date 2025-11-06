package ru.yandex.transfer.transfer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.accounts.dto.AccountDto;
import ru.yandex.api.AccountsClient;
import ru.yandex.transfer.TransferService;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class TransferServiceTest {
    @Mock
    private AccountsClient accountsClient;

    @InjectMocks
    private TransferService transferService;

    @BeforeEach
    void setUp() {
        transferService = new TransferService(accountsClient);
    }

    @Test
    void shouldTransferSuccessfully() {
        String fromLogin = "user1";
        String toLogin = "user2";
        Double amount = 1000.0;

        AccountDto accountFrom = AccountDto.builder()
                .login(fromLogin)
                .balance(5000.0)
                .errors(Collections.emptyList())
                .build();

        AccountDto accountTo = AccountDto.builder()
                .login(toLogin)
                .balance(2000.0)
                .errors(Collections.emptyList())
                .build();

        AccountDto updatedAccountFrom = AccountDto.builder()
                .login(fromLogin)
                .balance(4000.0)
                .errors(Collections.emptyList())
                .build();

        when(accountsClient.getAccount(fromLogin)).thenReturn(accountFrom);
        when(accountsClient.getAccount(toLogin)).thenReturn(accountTo);
        when(accountsClient.updateBalance(fromLogin, 4000.0)).thenReturn(updatedAccountFrom);
        when(accountsClient.updateBalance(toLogin, 3000.0)).thenReturn(accountTo);

        AccountDto result = transferService.transfer(fromLogin, amount, toLogin);

        assertThat(result.getLogin()).isEqualTo(fromLogin);
        assertThat(result.getBalance()).isEqualTo(4000.0);
        assertThat(result.getErrors()).isEmpty();
        verify(accountsClient).getAccount(fromLogin);
        verify(accountsClient).getAccount(toLogin);
        verify(accountsClient).updateBalance(fromLogin, 4000.0);
        verify(accountsClient).updateBalance(toLogin, 3000.0);
    }

    @Test
    void shouldReturnErrorWhenToLoginIsNull() {
        String fromLogin = "user1";
        Double amount = 1000.0;

        AccountDto result = transferService.transfer(fromLogin, amount, null);

        assertThat(result.getLogin()).isEqualTo(fromLogin);
        assertThat(result.getErrors()).containsExactly("Получатель не выбран");
        verifyNoInteractions(accountsClient);
    }

    @Test
    void shouldReturnErrorWhenAmountIsNull() {
        String fromLogin = "user1";
        String toLogin = "user2";

        AccountDto result = transferService.transfer(fromLogin, null, toLogin);

        assertThat(result.getLogin()).isEqualTo(fromLogin);
        assertThat(result.getErrors()).containsExactly("Сумма перевода должна быть положительной");
        verifyNoInteractions(accountsClient);
    }

    @Test
    void shouldReturnErrorWhenAmountIsZero() {
        String fromLogin = "user1";
        String toLogin = "user2";
        Double amount = 0.0;

        AccountDto result = transferService.transfer(fromLogin, amount, toLogin);

        assertThat(result.getLogin()).isEqualTo(fromLogin);
        assertThat(result.getErrors()).containsExactly("Сумма перевода должна быть положительной");
        verifyNoInteractions(accountsClient);
    }

    @Test
    void shouldReturnErrorWhenAmountIsNegative() {
        String fromLogin = "user1";
        String toLogin = "user2";
        Double amount = -100.0;

        AccountDto result = transferService.transfer(fromLogin, amount, toLogin);

        assertThat(result.getLogin()).isEqualTo(fromLogin);
        assertThat(result.getErrors()).containsExactly("Сумма перевода должна быть положительной");
        verifyNoInteractions(accountsClient);
    }

    @Test
    void shouldReturnErrorWhenInsufficientFunds() {
        String fromLogin = "user1";
        String toLogin = "user2";
        Double amount = 1000.0;

        AccountDto accountFrom = AccountDto.builder()
                .login(fromLogin)
                .balance(500.0)
                .errors(Collections.emptyList())
                .build();

        when(accountsClient.getAccount(fromLogin)).thenReturn(accountFrom);

        AccountDto result = transferService.transfer(fromLogin, amount, toLogin);


        assertThat(result.getLogin()).isEqualTo(fromLogin);
        assertThat(result.getErrors()).containsExactly("Недостаточно средств на счете");
        verify(accountsClient).getAccount(fromLogin);
        verify(accountsClient, never()).getAccount(toLogin);
        verify(accountsClient, never()).updateBalance(anyString(), anyDouble());
    }

    @Test
    void shouldReturnErrorWhenBalanceIsNull() {
        String fromLogin = "user1";
        String toLogin = "user2";
        Double amount = 1000.0;

        AccountDto accountFrom = AccountDto.builder()
                .login(fromLogin)
                .balance(null) // null баланс
                .errors(Collections.emptyList())
                .build();

        when(accountsClient.getAccount(fromLogin)).thenReturn(accountFrom);

        AccountDto result = transferService.transfer(fromLogin, amount, toLogin);

        assertThat(result.getLogin()).isEqualTo(fromLogin);
        assertThat(result.getErrors()).containsExactly("Недостаточно средств на счете");
        verify(accountsClient).getAccount(fromLogin);
        verify(accountsClient, never()).getAccount(toLogin);
        verify(accountsClient, never()).updateBalance(anyString(), anyDouble());
    }

    @Test
    void shouldHandlePreciseBalanceCalculations() {
        String fromLogin = "user1";
        String toLogin = "user2";
        Double amount = 123.45;

        AccountDto accountFrom = AccountDto.builder()
                .login(fromLogin)
                .balance(1000.0)
                .errors(Collections.emptyList())
                .build();

        AccountDto accountTo = AccountDto.builder()
                .login(toLogin)
                .balance(500.0)
                .errors(Collections.emptyList())
                .build();

        AccountDto updatedAccountFrom = AccountDto.builder()
                .login(fromLogin)
                .balance(876.55)
                .errors(Collections.emptyList())
                .build();

        when(accountsClient.getAccount(fromLogin)).thenReturn(accountFrom);
        when(accountsClient.getAccount(toLogin)).thenReturn(accountTo);
        when(accountsClient.updateBalance(fromLogin, 876.55)).thenReturn(updatedAccountFrom);
        when(accountsClient.updateBalance(toLogin, 623.45)).thenReturn(accountTo);

        AccountDto result = transferService.transfer(fromLogin, amount, toLogin);

        assertThat(result.getBalance()).isEqualTo(876.55);
        assertThat(result.getErrors()).isEmpty();
    }
}