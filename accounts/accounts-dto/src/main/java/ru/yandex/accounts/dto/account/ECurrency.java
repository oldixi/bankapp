package ru.yandex.accounts.dto.account;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public enum ECurrency {
    RUB("Рубли"),
    USD("Доллары"),
    CNY("Юани");

    private String currencyName;

    public String getCurrencyName() {
        return currencyName;
    }
}
