package ru.yandex.accounts.serv.account;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String> {
    boolean existsAccountByLoginIgnoreCaseAndCurrencyIgnoreCase(String login, String currency);

    Optional<Account> findAccountByLoginIgnoreCaseAndCurrencyIgnoreCase(String login, String currency);
}
