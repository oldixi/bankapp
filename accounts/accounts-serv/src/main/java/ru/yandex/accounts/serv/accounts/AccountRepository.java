package ru.yandex.accounts.serv.accounts;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String> {
    boolean existsAccountByLoginIgnoreCase(String login);

    Optional<Account> findAccountByLoginIgnoreCase(String login);

    List<Account> findAccountsByLoginIsNotIgnoreCase(String login);
}
