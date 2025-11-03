package ru.yandex.accounts.serv.accounts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String> {
    boolean existsAccountByLoginIgnoreCase(String login);

    Optional<Account> findAccountByLoginIgnoreCase(String login);

    List<Account> findAccountsByLoginIsNotIgnoreCase(String login);
}
