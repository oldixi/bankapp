package ru.yandex.front.api;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.accounts.dto.AccountDto;

@FeignClient(name = "cash", configuration = OAuth2FeignConfiguration.class)
public interface CashClient {

    @PostMapping("/api/cash/{login}")
    @CircuitBreaker(name = "cashService", fallbackMethod = "depositFallback")
    @Retry(name = "cashService", fallbackMethod = "depositFallback")
    AccountDto actionWithBalance(@PathVariable String login, @RequestParam Double amount, @RequestParam String action);

    default AccountDto actionWithBalance(String login, Double amount, String action, Throwable t) {
        throw new ServiceUnavailableException("Сервис операций с наличными временно недоступен. Невозможно пополнить счет.");
    }
}
