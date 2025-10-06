package ru.yandex.serv;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.yandex.serv.user.UserDto;

@Component
@RequiredArgsConstructor
public class FrontMapper {
    private final ModelMapper mapper;

    public UserDto toUserDto(NewUserDto entity) {
        return mapper.map(entity, UserDto.class);
    }
}
