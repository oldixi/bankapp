package ru.yandex.cash;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.yandex.accounts.dto.account.AccountDto;
import ru.yandex.accounts.dto.user.UserDto;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/cash")
@RequiredArgsConstructor
public class CashController {
    private final RestTemplate restTemplate;
    private final CashService cashService;
    private final static String IS_BLOCKED = "Операция заблокирована";

    @GetMapping("/{login}")
    public List<String> getCash(@PathVariable String login, @RequestBody CashRequestDto cash) {
        try {
            if (restTemplate.getForObject("http://${blockerUrl}/blocker", boolean.class)) {
                notificare(login, IS_BLOCKED);
                return Collections.singletonList(IS_BLOCKED);
            }
        } catch (Exception ignore) {}

        AccountDto account;
        try {
            account = restTemplate.getForObject("http://${accountsUrl}/accounts/" + login + "?currency=" +
                    cash.getCurrency(), AccountDto.class);
        } catch (Exception e) {
            return Collections.singletonList(e.getMessage());
        }

        CashResponseDto response;
        if (EAction.GET.equals(cash.getAction()))
            response = cashService.getCash(cash.getValue(), account.getAmount());
        else
            response = cashService.putCash(cash.getValue(), account.getAmount());
        if (response.getErrors() != null && !response.getErrors().isEmpty()) {
            notificare(login, response.getErrors().get(0));
            return response.getErrors();
        }

        account.setAmount(response.getAmount());
        try {
            return restTemplate.postForEntity("http://${accountsUrl}/accounts/" + login, account, AccountDto.class)
                    .getBody().getErrors(); //информинг будет внутри accounts
        } catch (Exception e) {
            return Collections.singletonList(e.getMessage());
        }
    }

    private void notificare(String login, String message) {
        String email = restTemplate.getForObject("http://${accountsUrl}/accounts/{login}", UserDto.class, login)
                .getEmail();
        restTemplate.postForObject("http://${notificationsUrl}/notifications/{login}",
                message, String.class, email);
    }
}
