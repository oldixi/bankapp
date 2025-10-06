package ru.yandex.serv;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.dto.AccountDto;
import ru.yandex.serv.account.AccountService;
import ru.yandex.dto.NewPasswordDto;
import ru.yandex.dto.UserDto;
import ru.yandex.serv.user.UserService;
import ru.yandex.dto.UserWithAccountsDto;

@RestController
@AllArgsConstructor
@RequestMapping("/accounts")
public class AccountsController {
    private final UserService userService;
    private final AccountService accountService;

    @PostMapping("/{login}/change-password")
    public UserDto changePassword(@RequestParam String login, @Valid @RequestBody NewPasswordDto pass) {
        return userService.changePassword(login, pass.getPassword(), pass.getConfirmPassword());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody UserDto user) {
        return userService.save(user);
    }

    @PostMapping("/{login}")
    public UserWithAccountsDto editUser(@RequestParam String login, @Valid @RequestBody UserWithAccountsDto user) {
        return userService.edit(login, user);
    }

    @GetMapping("/{login}")
    public UserDto getUserInfo(String login) {
        return userService.getUser(login);
    }

    @GetMapping("/account/{login}")
    public List<AccountDto> getAccounts(String login) {
        return accountService.getAccounts(login);
    }

    @PostMapping("/account")
    public AccountDto saveAccount(@Valid @RequestBody AccountDto account) {
        return accountService.save(account);
    }
}
