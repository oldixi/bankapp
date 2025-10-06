package ru.yandex.exchange.serv;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.yandex.exchange.dto.CurrencyRateDto;

@Component
@RequiredArgsConstructor
public class CurrentMapper {
    private final ModelMapper mapper;

    public CurrencyRateDto toDto(CurrencyRate entity) {
        return mapper.map(entity, CurrencyRateDto.class);
    }

    public CurrencyRate toEntity(CurrencyRateDto dto) {
        return mapper.map(dto, CurrencyRate.class);
    }
}
