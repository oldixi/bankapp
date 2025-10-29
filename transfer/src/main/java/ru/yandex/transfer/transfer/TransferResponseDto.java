package ru.yandex.transfer.transfer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferResponseDto {
    private Double accountFromBalance;
    private Double accountToBalance;
    private List<String> errors;
}
