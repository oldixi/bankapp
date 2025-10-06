package ru.yandex.accounts.serv.account;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.yandex.accounts.dto.account.AccountDto;
import ru.yandex.accounts.dto.account.ECurrency;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AccountMapper {
    private final ModelMapper mapper;

    public AccountDto toDto(Account entity) {
        AccountDto dto = mapper.map(entity, AccountDto.class);
        if (dto.getCurrency() != null) dto.setCurrencyName(ECurrency.valueOf(dto.getCurrency()).getCurrencyName());
        if (dto.getAmount() != null) dto.setExists(true);
        return dto;
    }

    public List<AccountDto> toDto(List<Account> entities) {
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }

    public Account toEntity(AccountDto dto) {
        return mapper.map(dto, Account.class);
    }
}
