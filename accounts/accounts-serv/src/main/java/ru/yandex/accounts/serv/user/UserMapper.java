package ru.yandex.accounts.serv.user;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.yandex.accounts.dto.user.UserDto;
import ru.yandex.accounts.dto.user.UserTransferDto;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final ModelMapper mapper;

    public UserDto toDto(User entity) {
        UserDto user = mapper.map(entity, UserDto.class);
        user.setPassword(null);
        user.setConfirmPassword(null);
        return user;
    }

    public UserTransferDto toTransferDto(User entity) {
        return mapper.map(entity, UserTransferDto.class);
    }

    public User toEntity(UserDto dto) {
        return mapper.map(dto, User.class);
    }
}
