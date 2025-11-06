package ru.yandex.cash;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.accounts.dto.AccountDto;

@RestController
@RequestMapping("/api/cash")
@RequiredArgsConstructor
public class CashController {
    private final CashService cashService;

    @PostMapping("/{login}/cash")
    public AccountDto actionWithBalance(@PathVariable String login, @RequestParam Double amount, @RequestParam String action) {
        return cashService.actionWithBalance(login, amount, EAction.valueOf(action));
    }
}
