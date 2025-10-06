package ru.yandex.exchange.serv;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "currency_rate")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyRate {
    @Id
    private String name;
    private String title;
    private double value;
}
