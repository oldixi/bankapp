package ru.yandex.accounts.serv.accounts;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.yandex.accounts.dto.AccountDto;
import ru.yandex.accounts.dto.NewAccountDto;
import ru.yandex.accounts.dto.AccountTransferDto;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountMapper {
    private final ModelMapper mapper;

    public AccountDto toDto(Account entity) {
        return mapper.map(entity, AccountDto.class);
    }

    public AccountDto toDto(NewAccountDto dto) {
        log.debug("toDto: newAccountDto={}", dto);
        AccountDto account = mapper.map(dto, AccountDto.class);
        log.trace("toDto: account={}", account);
        try {
            account.setBirthdate(LocalDate.parse(dto.getBirthdate()));
            log.trace("toDto: birthdate={}", account.getBirthdate());
        } catch (Exception e) {
            log.warn("toDto: can't convert birthdate with error {}", e.getMessage());
        }
        return account;
    }

    public List<AccountTransferDto> toTransferDto(List<Account> entities) {
        return entities.stream().map(this::toTransferDto).collect(Collectors.toList());
    }

    public Account toEntity(AccountDto dto) {
        return mapper.map(dto, Account.class);
    }

    public AccountDto toUserFrontDto(Account entity) {
        AccountDto user = mapper.map(entity, AccountDto.class);
        user.setPassword(null);
        return user;
    }

    public AccountTransferDto toTransferDto(Account entity) {
        return mapper.map(entity, AccountTransferDto.class);
    }
}
