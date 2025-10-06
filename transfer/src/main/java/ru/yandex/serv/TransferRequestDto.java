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
public class TransferRequestDto {
    private Double value;
    private ECurrency currencyFrom;
    private String loginTo;
    private ECurrency currencyTo;
}
