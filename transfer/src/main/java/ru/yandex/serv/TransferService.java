package ru.yandex.serv;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.serv.account.AccountDto;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class TransferService {
    public TransferResponseDto transfer(AccountDto accountFrom, AccountDto accountTo, Double value) {
        if (accountFrom.getAmount() == null || accountFrom.getAmount() - value < 0)
            return TransferResponseDto.builder()
                .errors(Collections.singletonList("Недостаточно средств на счете"))
                .build();

        Double amountFrom = accountFrom.getAmount() - value;
        accountFrom.setAmount(amountFrom);
        Double amountTo = accountTo.getAmount() + value;
        accountTo.setAmount(amountTo);
        return TransferResponseDto.builder()
                .accountFrom(accountFrom)
                .accountTo(accountTo)
                .build();
    }
}
