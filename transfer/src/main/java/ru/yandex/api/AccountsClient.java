package ru.yandex.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.yandex.accounts.dto.AccountDto;

import java.util.Collections;

@FeignClient(name = "accounts", fallback = AccountsClient.AccountsFallback.class, configuration = FeignConfig.class)
public interface AccountsClient {
    @GetMapping("/api/accounts/{login}")
    AccountDto getAccount(@PathVariable String login);

    @PostMapping("/api/accounts/{login}/balance")
    AccountDto updateBalance(@PathVariable String login, @RequestParam Double balance);

    @Component
    class AccountsFallback implements AccountsClient {
        @Override
        public AccountDto updateBalance(@PathVariable String login, @RequestParam Double balance) {
            return AccountDto.builder()
                    .login(login)
                    .errors(Collections.singletonList("Сервис лицевых счетов временно недоступен"))
                    .build();
        }

        @Override
        public AccountDto getAccount(@PathVariable String login) {
            return AccountDto.builder()
                    .login(login)
                    .errors(Collections.singletonList("Сервис лицевых счетов временно недоступен"))
                    .build();
        }
    }
}