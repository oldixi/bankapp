package ru.yandex.transfer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.accounts.dto.account.ECurrency;

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
