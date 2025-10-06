package ru.yandex.serv.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    public List<AccountDto> getAccounts(String login) {
        return Arrays.stream(ECurrency.values())
                        .map(eCurrency -> accountMapper.toDto(accountRepository
                                .findAccountByLoginIgnoreCaseAndCurrencyIgnoreCase(login, eCurrency.name())
                                .orElse(Account.builder()
                                        .currency(eCurrency.name())
                                        .login(login)
                                        .build())))
                        .collect(Collectors.toList());
    }

    public AccountDto getAccount(String login, String currency) {
        return accountMapper.toDto(accountRepository.findAccountByLoginIgnoreCaseAndCurrencyIgnoreCase(login, currency)
                .orElse(Account.builder()
                        .currency(currency)
                        .login(login)
                        .build()));
    }

    public AccountDto save(AccountDto data) {
        AccountDto accountFromDb = accountMapper.toDto(accountRepository
                .findAccountByLoginIgnoreCaseAndCurrencyIgnoreCase(data.getLogin(), data.getCurrency())
                .orElse(Account.builder()
                        .currency(data.getCurrency())
                        .login(data.getLogin())
                        .build()));
        List<String> errors = Stream.of(checkUniqueness(data.getLogin(), data.getCurrency()),
                        checkAmountNeg(data.getAmount()),
                        checkDeletionAbility(!data.isExists() && accountFromDb.isExists(),
                                data.getAmount(), data.getCurrencyName()))
                .filter(err -> !err.isBlank())
                .collect(Collectors.toList());
        if (!data.isExists() && accountFromDb.isExists() && data.getAmount().equals(0D)) {
            accountRepository.delete(accountMapper.toEntity(accountFromDb));
            accountFromDb.setAmount(null);
        }
        else if (data.isExists()) {
            accountRepository.save(accountMapper.toEntity(data));
            accountFromDb.setAmount(data.getAmount());
        }
        accountFromDb.setExists(data.isExists());
        accountFromDb.setErrors(errors);
        return accountFromDb;
    }

    public List<AccountDto> save(List<AccountDto> data) {
        return data.stream().map(this::save).collect(Collectors.toList());
    }

    private String checkUniqueness(String login, String currency) {
        if (login != null && accountRepository.existsAccountByLoginIgnoreCaseAndCurrencyIgnoreCase(login, currency))
            return "Для пользователя с логином " + login + " уже существует счет " + ECurrency.valueOf(currency).getCurrencyName();
        return "";
    }

    private String checkAmountNeg(Double amount) {
        if (amount != null && amount < 0) return "Сумма на счете не может быть отрицательной";
        return "";
    }

    private String checkDeletionAbility(boolean needToDelete, Double amount, String currencyName) {
        if (needToDelete && !amount.equals(0D))
            return "Удаление счета не возможно: баланс на счете " + currencyName + " не равен 0";
        return "";
    }
}
