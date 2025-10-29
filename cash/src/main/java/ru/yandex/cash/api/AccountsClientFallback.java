package ru.yandex.cash.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AccountsClientFallback {/*implements AccountsClient {
    @Override
    public AccountDto updateBalance(@PathVariable String login, @RequestParam Double balance) {
        log.error("Fallback: Unable to update balance for login: {}", login);
        return Optional.empty();
    }

    @Override
    public AccountDto getAccount(@PathVariable String login) {
        log.error("Fallback: Unable to get account for login: {}", login);
        return Optional.empty();
    }*/
}
