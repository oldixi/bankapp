package ru.yandex.transfer;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.yandex.accounts.dto.account.AccountDto;
import ru.yandex.accounts.dto.user.UserDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/transfer")
@RequiredArgsConstructor
public class TransferController {
    private final RestTemplate restTemplate;
    private final TransferService transferService;
    private final static String IS_BLOCKED = "Операция заблокирована";

    @GetMapping("/{login}/other")
    public List<String> transferOther(@PathVariable String login, @RequestBody TransferRequestDto cash) {
        try {
            List<String> blockingErrors = block(login);
            if (!blockingErrors.isEmpty()) return blockingErrors;
        } catch (Exception ignore) {}

        AccountDto accountFrom;
        AccountDto accountTo;
        try {
            accountFrom = restTemplate.getForObject("http://${accountsUrl}/accounts/" + login + "?currency=" +
                    cash.getCurrencyFrom().name(), AccountDto.class);
            accountTo = restTemplate.getForObject("http://${accountsUrl}/accounts/" + cash.getLoginTo() + "?currency=" +
                    cash.getCurrencyTo().name(), AccountDto.class);
        } catch (Exception e) {
            return Collections.singletonList(e.getMessage());
        }

        Double exchangedValue = cash.getValue();
        try {
            exchangedValue = exchange(exchangedValue, cash.getCurrencyFrom().name(), cash.getCurrencyTo().name());
        } catch (Exception e) {
            return Collections.singletonList(e.getMessage());
        }

        TransferResponseDto response = transferService.transfer(accountFrom, accountTo, exchangedValue);
        if (response.getErrors() != null && !response.getErrors().isEmpty()) {
            notificare(login, String.join("\n", response.getErrors()));
            return response.getErrors();
        }

        List<String> errors;
        String loginMessage = "";
        String otherLoginMessage = "";
        try {
            errors = new ArrayList<>(restTemplate.getForObject("http://${accountsUrl}/accounts/" + login + "?currency=" +
                    cash.getCurrencyFrom().name(), AccountDto.class).getErrors());
            errors.addAll(restTemplate.getForObject("http://${accountsUrl}/accounts/" + cash.getLoginTo() + "?currency=" +
                    cash.getCurrencyTo().name(), AccountDto.class).getErrors());
            loginMessage = response.getErrors().isEmpty() ?
                    ("Со счета списаны средства " + cash.getValue() + cash.getCurrencyFrom().name()) :
                    String.join("\n", response.getErrors());
            otherLoginMessage = response.getErrors().isEmpty() ?
                    ("На счет поступили средства " + exchangedValue + cash.getCurrencyTo().name()) : "";
        } catch (Exception e) {
            return Collections.singletonList(e.getMessage());
        }
        notificare(login, loginMessage);
        if (!otherLoginMessage.isBlank())
            notificare(cash.getLoginTo(), otherLoginMessage);
        return errors;
    }

    @GetMapping("/{login}")
    public List<String> transfer(@PathVariable String login, @RequestBody TransferRequestDto cash) {
        try {
            List<String> blockingErrors = block(login);
            if (!blockingErrors.isEmpty()) return blockingErrors;
        } catch (Exception ignore) {}

        AccountDto account;
        try {
            account = restTemplate.getForObject("http://${accountsUrl}/accounts/" + login + "?currency=" +
                    cash.getCurrencyFrom().name(), AccountDto.class);
        } catch (Exception e) {
            return Collections.singletonList(e.getMessage());
        }

        Double exchangedValue = cash.getValue();
        try {
            exchangedValue = exchange(exchangedValue, cash.getCurrencyFrom().name(), cash.getCurrencyTo().name());
        } catch (Exception e) {
            return Collections.singletonList(e.getMessage());
        }

        TransferResponseDto response = transferService.transfer(account, account, exchangedValue);
        if (response.getErrors() != null && !response.getErrors().isEmpty()) {
            notificare(login, String.join("\n", response.getErrors()));
            return response.getErrors();
        }

        List<String> errors;
        String message = "";
        try {
            errors = new ArrayList<>(restTemplate.getForObject("http://${accountsUrl}/accounts/" + login + "?currency=" +
                    cash.getCurrencyFrom().name(), AccountDto.class).getErrors());
            message = response.getErrors().isEmpty() ?
                    ("Со счета списаны средства " + cash.getValue() + cash.getCurrencyFrom().name()
                            + ".\n На счет поступили средства " + exchangedValue + cash.getCurrencyTo().name()) :
                    String.join("\n", response.getErrors());
        } catch (Exception e) {
            return Collections.singletonList(e.getMessage());
        }
        notificare(login, message);
        return errors;
    }

    private void notificare(String login, String message) {
        String email = restTemplate.getForObject("http://${accountsUrl}/accounts/{login}", UserDto.class, login)
                .getEmail();
        restTemplate.postForObject("http://${notificationsUrl}/notifications/email/{email}/send",
                message, String.class, email);
    }

    private Double exchange(Double exchangedValue, String currencyFrom, String currencyTo) {
        return restTemplate.getForObject("http://${exchangeUrl}/exchange/?value=" + exchangedValue +
                "&currencyFrom=" + currencyFrom + "&currencyTo=" + currencyTo, Double.class);
    }

    private List<String> block(String login) {
        if (restTemplate.getForObject("http://${blockerUrl}/blocker", boolean.class)) {
            notificare(login, IS_BLOCKED);
            return Collections.singletonList(IS_BLOCKED);
        }
        return new ArrayList<>();
    }
}
