package ru.yandex.serv;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CashService {
    public CashResponseDto getCash(Double value, Double amount) {
        if (amount == null || amount - value < 0) return CashResponseDto.builder()
                .amount(amount)
                .errors(Collections.singletonList("Недостаточно средств на счете"))
                .build();
        return CashResponseDto.builder()
                .amount(amount - value)
                .build();
    }

    public CashResponseDto putCash(Double value, Double amount) {
        return CashResponseDto.builder()
                .amount(amount == null ? value : amount + value)
                .build();
    }
}
