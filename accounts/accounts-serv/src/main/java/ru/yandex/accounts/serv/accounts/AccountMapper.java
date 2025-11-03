package ru.yandex.accounts.serv.accounts;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.yandex.accounts.dto.AccountDto;
import ru.yandex.accounts.dto.EUserRole;
import ru.yandex.accounts.dto.RoleDto;
import ru.yandex.accounts.dto.NewAccountDto;
import ru.yandex.accounts.dto.AccountTransferDto;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountMapper {
    private final ModelMapper mapper;

    public AccountDto toDto(Account entity) {
        return mapper.map(entity, AccountDto.class);
    }

    public AccountDto toDto(NewAccountDto dto) {
        log.info("toDto: newAccountDto={}", dto);
        AccountDto account = mapper.map(dto, AccountDto.class);
        log.info("toDto: account={}", account);
        try {
            account.setBirthdate(LocalDate.parse(dto.getBirthdate()));
            log.info("toDto: birthdate={}", account.getBirthdate());
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

    public Account toEntity(NewAccountDto dto) {
        log.info("toEntity: newAccountDto={}", dto);
        Account account = mapper.map(dto, Account.class);
        log.info("toEntity: account={}", account);
        try {
            account.setBirthdate(LocalDate.parse(dto.getBirthdate()));
            log.info("toEntity: birthdate={}", account.getBirthdate());
        } catch (Exception e) {
            log.warn("toEntity: can't convert birthdate with error {}", e.getMessage());
        }
        return account;
    }

    public AccountDto toUserFrontDto(Account entity) {
        AccountDto user = mapper.map(entity, AccountDto.class);
        user.setPassword(null);
        return user;
    }

    public AccountTransferDto toTransferDto(Account entity) {
        return mapper.map(entity, AccountTransferDto.class);
    }

    public NewAccountDto toUserSecurityDto(Account user) {
        NewAccountDto newAccountDto = mapper.map(user, NewAccountDto.class);
        //log.info("Start toUserDto: user={}, userDto={}", user, userDto);
        newAccountDto.setAuthorities(Stream.of(EUserRole.ROLE_USER.name())
                .map(role -> {
                    EUserRole roleStr = EUserRole.UNAUTHORIZED;
                    try {
                        roleStr = EUserRole.valueOf(role);
                    } catch (Exception ignore) {}
                    return new RoleDto(roleStr);
                }).toList());
        //userDto.getAuthorities().forEach(auth -> log.info("Finish toUserDto: auth={}", auth.getAuthority()));
        return newAccountDto;
    }
}
