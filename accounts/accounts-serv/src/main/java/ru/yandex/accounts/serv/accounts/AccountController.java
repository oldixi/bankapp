package ru.yandex.accounts.serv.accounts;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.accounts.dto.AccountDto;
import ru.yandex.accounts.dto.AccountTransferDto;
import ru.yandex.accounts.dto.NewAccountDto;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountService accountService;

    @GetMapping("/{login}/login")
    public ResponseEntity<AccountDto> loadUserByUsername(@PathVariable String login) {
        return ResponseEntity.ok(accountService.loadUserByUsername(login));
    }

    @PostMapping("/signup")
    public ResponseEntity<AccountDto> register(@Valid @RequestBody NewAccountDto request) {
        return ResponseEntity.ok(accountService.saveNewAccount(request));
    }

    @GetMapping("/{login}")
    public ResponseEntity<AccountDto> getAccount(@PathVariable String login) {
        return ResponseEntity.ok(accountService.getAccount(login));
    }

    @PostMapping("/{login}/password")
    public ResponseEntity<AccountDto> updatePassword(@PathVariable String login,
                                                     @RequestParam String password,
                                                     @RequestParam String confirmPassword) {
        return ResponseEntity.ok(accountService.changePassword(login, password, confirmPassword));
    }

    @PostMapping("/{login}/edit")
    public ResponseEntity<AccountDto> updateUser(@PathVariable String login,
                                                 @RequestParam(required = false) String name,
                                                 @RequestParam(required = false) String email,
                                                 @RequestParam(required = false) String birthdate) {
        return ResponseEntity.ok(accountService.editUserInfo(login, name, email, birthdate));
    }

    @PostMapping("/{login}/balance")
    public ResponseEntity<AccountDto> updateBalance(@PathVariable String login, @RequestParam Double balance) {
        return ResponseEntity.ok(accountService.saveNewBalance(login, balance));
    }

    @GetMapping("/{login}/transfer")
    public ResponseEntity<List<AccountTransferDto>> getAccountsForTransfer(@PathVariable String login) {
        return ResponseEntity.ok(accountService.getAccounts(login));
    }

    @PostMapping("/{login}/delete")
    public ResponseEntity<AccountDto> deleteAccount(@PathVariable String login) {
        return ResponseEntity.ok(accountService.delete(login));
    }
}
