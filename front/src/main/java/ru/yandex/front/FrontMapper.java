package ru.yandex.front;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.yandex.accounts.dto.user.UserDto;

@Component
@RequiredArgsConstructor
public class FrontMapper {
    private final ModelMapper mapper;

    public UserDto toUserDto(NewUserDto entity) {
        return mapper.map(entity, UserDto.class);
    }
}
