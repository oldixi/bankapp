package ru.yandex.transfer.transfer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.accounts.dto.AccountDto;
import ru.yandex.transfer.api.AccountsClient;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class TransferService {
    private final AccountsClient accountsClient;

    public AccountDto transfer(String login, Double amount, String toLogin) {
        if (toLogin == null)
            return AccountDto.builder()
                    .login(login)
                    .errors(Collections.singletonList("Получатель не выбран"))
                    .build();

        if (amount == null || amount <= 0)
            return AccountDto.builder()
                    .login(login)
                    .errors(Collections.singletonList("Сумма перевода должна быть положительной"))
                    .build();

        AccountDto accountFrom = getAccount(login);
        if (accountFrom.getBalance() == null || accountFrom.getBalance() - amount < 0)
            return AccountDto.builder()
                    .login(login)
                    .errors(Collections.singletonList("Недостаточно средств на счете"))
                    .build();

        Double balanceFrom = accountFrom.getBalance() - amount;
        AccountDto updatedAccountFrom = updateBalance(login, balanceFrom);
        if (!updatedAccountFrom.getErrors().isEmpty()) return updatedAccountFrom;

        AccountDto accountTo = getAccount(toLogin);
        Double balanceTo = accountTo.getBalance() + amount;
        updateBalance(toLogin, balanceTo);

        return updatedAccountFrom;
    }

    private AccountDto getAccount(String userLogin) {
        try {
            return accountsClient.getAccount(userLogin);
        } catch (Exception e) {
            return AccountDto.builder()
                    .login(userLogin)
                    .errors(Collections.singletonList("Сервис лицевых счетов временно недоступен: " + e.getMessage()))
                    .build();
        }
    }

    private AccountDto updateBalance(String userLogin, Double balance) {
        try {
            return accountsClient.updateBalance(userLogin, balance);
        } catch (Exception e) {
            return AccountDto.builder()
                    .login(userLogin)
                    .errors(Collections.singletonList("Сервис лицевых счетов временно недоступен: " + e.getMessage()))
                    .build();
        }
    }
}
