package ru.yandex.cash;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.exchange.dto.ECurrency;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CashRequestDto {
    private Double value;
    private ECurrency currency;
    private EAction action;
}
