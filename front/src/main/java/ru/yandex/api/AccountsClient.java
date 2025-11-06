package ru.yandex.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.yandex.accounts.dto.AccountDto;
import ru.yandex.accounts.dto.AccountTransferDto;
import ru.yandex.accounts.dto.NewAccountDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@FeignClient(name = "accounts", fallback = AccountsClient.AccountsFallback.class)
public interface AccountsClient {
    @GetMapping("/api/accounts/{login}/login")
    AccountDto loadUserByUsername(@PathVariable String login);

    @PostMapping("/api/accounts/signup")
    AccountDto register(@RequestBody NewAccountDto newAccount);

    @GetMapping("/api/accounts/{login}")
    AccountDto getAccount(@PathVariable String login);

    @PostMapping("/api/accounts/{login}/password")
    AccountDto updateAccountPassword(@PathVariable String login,
                                     @RequestParam String password,
                                     @RequestParam String confirmPassword);

    @PostMapping("/api/accounts/{login}/edit")
    AccountDto updateAccount(@PathVariable String login,
                             @RequestParam String name,
                             @RequestParam String email,
                             @RequestParam String birthdate);

    @PostMapping("/api/accounts/{login}/delete")
    AccountDto deleteAccount(@PathVariable String login);

    @GetMapping("/api/accounts/{login}/transfer")
    List<AccountTransferDto> getAccounts(@PathVariable String login);

    @Component
    class AccountsFallback implements AccountsClient {
        @Override
        public AccountDto loadUserByUsername(@PathVariable String login) {
            return AccountDto.builder()
                    .login(login)
                    .errors(Collections.singletonList("Сервис лицевых счетов временно недоступен"))
                    .build();
        }

        @Override
        public AccountDto register(@RequestBody NewAccountDto newAccount) {
            return AccountDto.builder()
                    .login(newAccount.getLogin())
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

        @Override
        public AccountDto updateAccountPassword(@PathVariable String login,
                                         @RequestParam String password,
                                         @RequestParam String confirmPassword) {
            return AccountDto.builder()
                    .login(login)
                    .errors(Collections.singletonList("Сервис лицевых счетов временно недоступен"))
                    .build();
        }

        @Override
        public AccountDto updateAccount(@PathVariable String login,
                                 @RequestParam String name,
                                 @RequestParam String email,
                                 @RequestParam String birthdate) {
            return AccountDto.builder()
                    .login(login)
                    .errors(Collections.singletonList("Сервис лицевых счетов временно недоступен"))
                    .build();
        }

        @Override
        public AccountDto deleteAccount(@PathVariable String login) {
            return AccountDto.builder()
                    .login(login)
                    .errors(Collections.singletonList("Сервис лицевых счетов временно недоступен"))
                    .build();
        }

        @Override
        public List<AccountTransferDto> getAccounts(@PathVariable String login) {
            return new ArrayList<>();
        }
    }
}
