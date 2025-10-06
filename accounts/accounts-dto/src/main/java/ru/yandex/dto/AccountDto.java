package ru.yandex.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
    private String login;
    private String email;
    private String currency;
    private String currencyName;
    private Double amount;
    private boolean exists;
    private List<String> errors = new ArrayList<>();
}
