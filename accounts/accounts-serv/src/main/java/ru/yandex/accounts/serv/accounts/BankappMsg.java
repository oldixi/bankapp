package ru.yandex.accounts.serv.accounts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BankappMsg {
    private String email;
    private String message;
}
