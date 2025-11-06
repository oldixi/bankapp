package ru.yandex.transfer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.accounts.dto.AccountDto;
import ru.yandex.api.AccountsClient;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferService {
    private final AccountsClient accountsClient;

    public AccountDto transfer(String login, Double amount, String toLogin) {
        log.info("transfer: amount={} from {} to {}", amount, login, toLogin);
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
        log.info("transfer: accountFrom={}", accountFrom);
        if (accountFrom.getBalance() == null || accountFrom.getBalance() - amount < 0)
            return AccountDto.builder()
                    .login(login)
                    .errors(Collections.singletonList("Недостаточно средств на счете"))
                    .build();

        Double balanceFrom = accountFrom.getBalance() - amount;
        AccountDto updatedAccountFrom = updateBalance(login, balanceFrom);
        if (!updatedAccountFrom.getErrors().isEmpty()) return updatedAccountFrom;

        AccountDto accountTo = getAccount(toLogin);
        log.info("transfer: accountTo={}", accountTo);
        Double balanceTo = accountTo.getBalance() + amount;
        updateBalance(toLogin, balanceTo);
        log.info("transfer: after update balance accountTo={}", accountTo);
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
