package ru.yandex.front.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.accounts.dto.AccountDto;

import java.util.Collections;

@FeignClient(name = "cash", fallback = CashClient.CashFallback.class/*, configuration = OAuth2FeignConfiguration.class*/)
public interface CashClient {
    @PostMapping("/api/cash/{login}/cash")
    AccountDto actionWithBalance(@PathVariable String login, @RequestParam Double amount, @RequestParam String action);

    @Component
    class CashFallback implements CashClient {
        @Override
        public AccountDto actionWithBalance(@PathVariable String login, @RequestParam Double amount, @RequestParam String action) {
            return AccountDto.builder()
                    .login(login)
                    .errors(Collections.singletonList("Сервис операций с наличными временно недоступен"))
                    .build();
        }
    }
}
