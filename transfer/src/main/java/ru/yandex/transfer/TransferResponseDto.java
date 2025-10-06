package ru.yandex.transfer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.accounts.dto.account.AccountDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferResponseDto {
    private AccountDto accountFrom;
    private AccountDto accountTo;
    private List<String> errors;
}
