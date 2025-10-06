package ru.yandex.accounts.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private String name;
    private String login;
    private String password;
    private String confirmPassword;
    private String email;
    private LocalDate birthdate;
    private List<String> errors;
}
