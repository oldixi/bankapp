package ru.yandex.front.api;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.accounts.dto.AccountDto;
import ru.yandex.accounts.dto.AccountTransferDto;
import ru.yandex.accounts.dto.NewAccountDto;

import java.util.Collections;
import java.util.List;

@FeignClient(name = "accounts", configuration = OAuth2FeignConfiguration.class)
public interface AccountsClient {
    @GetMapping("/api/accounts/{login}/login")
    @CircuitBreaker(name = "accountsService", fallbackMethod = "loadUserByUsernameFallback")
    @Retry(name = "accountsService", fallbackMethod = "loadUserByUsernameFallback")
    AccountDto /*UserDetails*/ loadUserByUsername(@PathVariable String login);

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
    @CircuitBreaker(name = "accountsService", fallbackMethod = "updateAccountFallback")
    @Retry(name = "accountsService", fallbackMethod = "updateAccountFallback")
    AccountDto updateAccount(@PathVariable String login,
                             @RequestParam String name,
                             @RequestParam String email,
                             @RequestParam String birthdate);

    @PostMapping("/api/accounts/{login}/delete")
    @CircuitBreaker(name = "accountsService", fallbackMethod = "deleteFallback")
    @Retry(name = "accountsService", fallbackMethod = "deleteFallback")
    AccountDto deleteAccount(@PathVariable String login);

    @GetMapping("/api/accounts/{login}/transfer")
    @CircuitBreaker(name = "accountsService", fallbackMethod = "getAccountFallback")
    @Retry(name = "accountsService", fallbackMethod = "getAccountFallback")
    List<AccountTransferDto> getAccounts(@PathVariable String login);

    default AccountDto loadUserByUsernameFallback(NewAccountDto newAccount, Throwable t) {
        throw new ServiceUnavailableException("Сервис аккаунтов временно недоступен. Вход в приложение невозможен.");
    }

    default AccountDto registerFallback(NewAccountDto newAccount, Throwable t) {
        throw new ServiceUnavailableException("Сервис аккаунтов временно недоступен. Регистрация невозможна.");
    }

    default AccountDto getAccountFallback(String login, Throwable t) {
        throw new ServiceUnavailableException("Сервис аккаунтов временно недоступен. Невозможно получить данные.");
    }

    default AccountDto updatePasswordFallback(String login, String password, String confirmPassword, Throwable t) {
        throw new ServiceUnavailableException("Сервис аккаунтов временно недоступен. Невозможно изменить пароль.");
    }

    default AccountDto updateAccountFallback(String login, String name, String email, String birthdate, Throwable t) {
        throw new ServiceUnavailableException("Сервис аккаунтов временно недоступен. Невозможно обновить профиль.");
    }

    default AccountDto deleteFallback(NewAccountDto newAccount, Throwable t) {
        throw new ServiceUnavailableException("Сервис аккаунтов временно недоступен. Удаление аккаунта невозможно.");
    }

    default List<AccountTransferDto> getAccountsFallback(String login, Throwable t) {
        return Collections.emptyList();
    }
}
