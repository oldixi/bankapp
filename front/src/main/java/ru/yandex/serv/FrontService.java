package ru.yandex.serv;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.yandex.serv.user.NewPasswordDto;
import ru.yandex.serv.user.UserDto;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FrontService {
    private final RestTemplate restTemplate;
    private final FrontMapper frontMapper;

    public List<String> registerUser(NewUserDto user) {
        UserDto userToSave = frontMapper.toUserDto(user);
        try {
            return restTemplate.postForEntity("http://${accountsUrl}/accounts", userToSave, UserDto.class)
                    .getBody().getErrors();
        } catch (Exception e) {
            return Collections.singletonList(e.getMessage());
        }
    }

    public List<String> changePassword(String login, String password, String confirmPassword) {
        NewPasswordDto pass = new NewPasswordDto(password, confirmPassword);
        try {
            return restTemplate.postForEntity("http://${accountsUrl}/accounts/" + login + "/change-password",
                    pass, UserDto.class).getBody().getErrors();
        } catch (Exception e) {
            return Collections.singletonList(e.getMessage());
        }
    }

    public List<String> editAccountInfo(String login, UserDto user) {
        try {
            return restTemplate.postForEntity("http://${accountsUrl}/accounts/" + login + "/edit",
                    user, UserDto.class).getBody().getErrors();
        } catch (Exception e) {
            return Collections.singletonList(e.getMessage());
        }
    }

    public List<String> cash(String login, CashRequestDto cashRequestDto) {
        try {
            return restTemplate.postForEntity("http://${cashUrl}/cash/" + login,
                    cashRequestDto, CashResponseDto.class).getBody().getErrors();
        } catch (Exception e) {
            return Collections.singletonList(e.getMessage());
        }
    }

    public List<String> transfer(String login, TransferRequestDto user) {
        try {
            return restTemplate.postForEntity("http://${transferUrl}/transfer/" + login,
                    user, UserDto.class).getBody().getErrors();
        } catch (Exception e) {
            return Collections.singletonList(e.getMessage());
        }
    }

    public List<String> transferOther(String login, TransferRequestDto user) {
        try {
            return restTemplate.postForEntity("http://${transferUrl}/transfer/" + login + "/other",
                    user, UserDto.class).getBody().getErrors();
        } catch (Exception e) {
            return Collections.singletonList(e.getMessage());
        }
    }
}
