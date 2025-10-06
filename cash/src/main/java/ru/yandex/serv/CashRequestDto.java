package ru.yandex.serv;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.serv.account.ECurrency;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CashRequestDto {
    private Double value;
    private ECurrency currency;
    private EAction action;
}
