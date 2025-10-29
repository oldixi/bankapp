package ru.yandex.front.api;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.accounts.dto.AccountDto;

@FeignClient(name = "transfer", configuration = OAuth2FeignConfiguration.class)
public interface TransferClient {

    @PostMapping("/api/transfer/{login}/transfer-other}")
    @CircuitBreaker(name = "transferService", fallbackMethod = "executeTransferFallback")
    @Retry(name = "transferService", fallbackMethod = "executeTransferFallback")
    AccountDto transfer(@PathVariable String login, @RequestParam Double amount, @RequestParam String toLogin);

    default void executeTransferFallback(String login, Double amount, String toLogin, Throwable t) {
        throw new ServiceUnavailableException("Сервис переводов временно недоступен. Невозможно выполнить перевод.");
    }
}