package ru.yandex.front.api;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.accounts.dto.AccountDto;
import ru.yandex.accounts.dto.AccountTransferDto;
import ru.yandex.accounts.dto.NewAccountDto;
import ru.yandex.accounts.serv.accounts.Account;

import java.util.Collections;
import java.util.List;

@FeignClient(name = "accounts", configuration = OAuth2FeignConfiguration.class)
public interface AccountsClient {

    @PostMapping("/api/accounts/signup")
    @CircuitBreaker(name = "accountsService", fallbackMethod = "registerFallback")
    @Retry(name = "accountsService", fallbackMethod = "registerFallback")
    AccountDto register(@RequestBody NewAccountDto newAccount);

    @GetMapping("/api/accounts/{login}")
    @CircuitBreaker(name = "accountsService", fallbackMethod = "getAccountFallback")
    @Retry(name = "accountsService", fallbackMethod = "getAccountFallback")
    AccountDto getAccount(@PathVariable String login);

    @PostMapping("/api/accounts/{login}/password")
    @CircuitBreaker(name = "accountsService", fallbackMethod = "updatePasswordFallback")
    @Retry(name = "accountsService", fallbackMethod = "updatePasswordFallback")
    AccountDto updateAccountPassword(@PathVariable String login,
                                     @RequestParam String password,
                                     @RequestParam String confirmPassword);

    @PostMapping("/api/accounts/{login}/edit")
    @CircuitBreaker(name = "accountsService", fallbackMethod = "updateProfileFallback")
    @Retry(name = "accountsService", fallbackMethod = "updateProfileFallback")
    AccountDto updateAccount(@PathVariable String login,
                             @RequestParam String name,
                             @RequestParam String email,
                             @RequestParam String birthdate);

    @DeleteMapping("/api/accounts/{login}")
    @CircuitBreaker(name = "accountsService", fallbackMethod = "updatePasswordFallback")
    @Retry(name = "accountsService", fallbackMethod = "updatePasswordFallback")
    AccountDto deleteAccount(@PathVariable String login);

    @GetMapping("/api/accounts/{login}/transfer")
    @CircuitBreaker(name = "accountsService", fallbackMethod = "getAccountFallback")
    @Retry(name = "accountsService", fallbackMethod = "getAccountFallback")
    List<AccountTransferDto> getAccounts(@PathVariable String login);

    default Account registerFallback(NewAccountDto newAccount, Throwable t) {
        throw new ServiceUnavailableException("Сервис аккаунтов временно недоступен. Регистрация невозможна.");
    }

    default Account getAccountFallback(String login, Throwable t) {
        throw new ServiceUnavailableException("Сервис аккаунтов временно недоступен. Невозможно получить данные.");
    }

    default void updatePasswordFallback(String login, String password, String confirmPassword, Throwable t) {
        throw new ServiceUnavailableException("Сервис аккаунтов временно недоступен. Невозможно изменить пароль.");
    }

    default Account updateProfileFallback(String login, String name, String email, String birthdate, Throwable t) {
        throw new ServiceUnavailableException("Сервис аккаунтов временно недоступен. Невозможно обновить профиль.");
    }

    default List<Account> getAccountsFallback(String login, Throwable t) {
        return Collections.emptyList();
    }
}
