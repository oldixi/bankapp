package ru.yandex.serv;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public enum ECurrency {
    RUB("Рубли"),
    USD("Доллары"),
    CNY("Юани");

    private String currencyName;

    public String getCurrencyName() {
        return currencyName;
    }
}
