package ru.yandex.cash.cash;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.accounts.dto.AccountDto;
import ru.yandex.cash.api.AccountsClient;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class CashService {
    private final AccountsClient accountsClient;

    public AccountDto actionWithBalance(String userLogin, Double amount, EAction action) {
        log.info("actionWithBalance: userLogin={}, amount={}, action={}", userLogin, amount, action);
        AccountDto account = getAccount(userLogin);
        CashResponseDto response;
        switch(action) {
            case PUT -> response = depositCashBalance(account.getBalance(), amount);
            case GET -> response = withdrawCashBalance(account.getBalance(), amount);
            default -> response = new CashResponseDto(account.getBalance(),
                    Collections.singletonList("Тип операции не поддерживается"));
        }
        if (response.getErrors() == null || response.getErrors().isEmpty()) {
            account.setBalance(response.getBalance());
            return updateBalance(userLogin, response.getBalance());
        }
        return AccountDto.builder().login(userLogin).errors(response.getErrors()).build();
    }

    private CashResponseDto withdrawCashBalance(Double balance, Double amount) {
        if (amount == null || amount <= 0)
            return CashResponseDto.builder()
                    .balance(balance)
                    .errors(Collections.singletonList("Сумма перевода должна быть положительной"))
                    .build();
        if (balance == null || balance - amount < 0) {
            return CashResponseDto.builder()
                    .balance(balance)
                    .errors(Collections.singletonList("Недостаточно средств на счете"))
                    .build();
        }
        return CashResponseDto.builder()
                .balance(balance - amount)
                .build();
    }

    private CashResponseDto depositCashBalance(Double balance, Double amount) {
        if (amount == null || amount <= 0)
            return CashResponseDto.builder()
                    .balance(balance)
                    .errors(Collections.singletonList("Сумма перевода должна быть положительной"))
                    .build();
        return CashResponseDto.builder()
                .balance(balance == null ? amount : balance + amount)
                .build();
    }

    private AccountDto getAccount(String userLogin) {
        try {
            return accountsClient.getAccount(userLogin);
        } catch (Exception e) {
            return AccountDto.builder().login(userLogin).errors(Collections.singletonList(e.getMessage())).build();
        }
    }

    private AccountDto updateBalance(String userLogin, Double balance) {
        try {
            return accountsClient.updateBalance(userLogin, balance);
        } catch (Exception e) {
            return AccountDto.builder().login(userLogin).errors(Collections.singletonList(e.getMessage())).build();
        }
    }
}
