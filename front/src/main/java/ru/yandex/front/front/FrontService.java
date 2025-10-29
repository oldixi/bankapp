package ru.yandex.front.front;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.accounts.dto.AccountDto;
import ru.yandex.accounts.dto.AccountTransferDto;
import ru.yandex.cash.cash.CashResponseDto;
import ru.yandex.accounts.dto.NewAccountDto;
import ru.yandex.front.api.AccountsClient;
import ru.yandex.front.api.CashClient;
import ru.yandex.front.api.TransferClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FrontService {
    private final AccountsClient accountsClient;
    private final CashClient cashClient;
    private final TransferClient transferClient;

    public AccountDto registerUser(String login,
                                   String password,
                                   String confirmPassword,
                                   String name,
                                   String email,
                                   String birthdate) {
        NewAccountDto userToSave = NewAccountDto.builder()
                .login(login)
                .email(email)
                .password(password)
                .confirmPassword(confirmPassword)
                .name(name)
                .birthdate(birthdate)
                .build();
        try {
            return accountsClient.register(userToSave);
        } catch (Exception e) {
            return AccountDto.builder().login(login).errors(Collections.singletonList(e.getMessage())).build();
        }
    }

    public AccountDto getAccount(String userLogin) {
        try {
            return accountsClient.getAccount(userLogin);
        } catch (Exception e) {
            return AccountDto.builder().login(userLogin).errors(Collections.singletonList(e.getMessage())).build();
        }
    }

    public List<AccountTransferDto> getAccountsForTransfer(String userLogin) {
        try {
            return accountsClient.getAccounts(userLogin);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public AccountDto changePassword(String login, String password, String confirmPassword) {
        try {
            return accountsClient.updateAccountPassword(login, password, confirmPassword);
        } catch (Exception e) {
            return AccountDto.builder().login(login).errors(Collections.singletonList(e.getMessage())).build();
        }
    }

    public AccountDto editAccountInfo(String login, String name, String email, String birthdate) {
        try {
            return accountsClient.updateAccount(login, name, email, birthdate);
        } catch (Exception e) {
            return AccountDto.builder().login(login).errors(Collections.singletonList(e.getMessage())).build();
        }
    }

    public AccountDto cash(String login, Double amount, String action) {
        try {
            return cashClient.actionWithBalance(login, amount, action);
        } catch (Exception e) {
            return AccountDto.builder().login(login).errors(Collections.singletonList(e.getMessage())).build();
        }
    }

    public AccountDto transfer(String login, Double amount, String toLogin) {
        try {
            return transferClient.transfer(login, amount, toLogin);
        } catch (Exception e) {
            return AccountDto.builder().login(login).errors(Collections.singletonList(e.getMessage())).build();
        }
    }

    public AccountDto delete(String login) {
        try {
            return accountsClient.deleteAccount(login);
        } catch (Exception e) {
            return AccountDto.builder().login(login).errors(Collections.singletonList(e.getMessage())).build();
        }
    }
}
