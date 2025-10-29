package ru.yandex.transfer.transfer;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.accounts.dto.AccountDto;

@RestController
@RequestMapping("/api/transfer")
@RequiredArgsConstructor
public class TransferController {
    private final TransferService transferService;

    @PostMapping("/{login}/transfer-other")
    public AccountDto transfer(@PathVariable String login, @RequestParam Double amount, @RequestParam String toLogin) {
        return transferService.transfer(login, amount, toLogin);
    }
}
