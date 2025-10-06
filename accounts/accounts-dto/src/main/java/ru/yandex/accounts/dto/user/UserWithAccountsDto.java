package ru.yandex.accounts.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.accounts.dto.account.AccountDto;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserWithAccountsDto {
    private UserDto user;
    private List<AccountDto> accounts = new ArrayList<>();
    private List<String> errors = new ArrayList<>();
}
