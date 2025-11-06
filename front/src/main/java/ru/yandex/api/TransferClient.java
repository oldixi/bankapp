package ru.yandex.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.accounts.dto.AccountDto;

import java.util.Collections;

@FeignClient(name = "transfer", fallback = TransferClient.TransferFallback.class)
public interface TransferClient {
    @PostMapping("/api/transfer/{login}/transfer-other")
    AccountDto transfer(@PathVariable String login, @RequestParam Double amount, @RequestParam String toLogin);

    @Component
    class TransferFallback implements TransferClient {
        @Override
        public AccountDto transfer(@PathVariable String login, @RequestParam Double amount, @RequestParam String toLogin) {
            return AccountDto.builder()
                    .login(login)
                    .errors(Collections.singletonList("Сервис перевода средств временно недоступен"))
                    .build();
        }
    }
}