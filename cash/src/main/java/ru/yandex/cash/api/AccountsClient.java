package ru.yandex.cash.api;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.accounts.dto.AccountDto;

@FeignClient(name = "accounts", configuration = OAuth2FeignConfiguration.class)
@CircuitBreaker(name = "accountsService", fallbackMethod = "getAccountFallback")
@Retry(name = "accountsService", fallbackMethod = "getAccountFallback")
public interface AccountsClient {
    @GetMapping("/api/accounts/{login}")
    AccountDto getAccount(@PathVariable String login);

    @PostMapping("/api/accounts/{login}/balance")
    AccountDto updateBalance(@PathVariable String login, @RequestParam Double balance);

    default AccountDto getAccountFallback(String login, Throwable t) {
        throw new ServiceUnavailableException("Сервис аккаунтов временно недоступен. Невозможно получить данные.");
    }

    default AccountDto updateBalanceFallback(String login, Double balance, Throwable t) {
        throw new ServiceUnavailableException("Сервис аккаунтов временно недоступен. Невозможно получить данные.");
    }
}
