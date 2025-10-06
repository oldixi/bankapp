package ru.yandex.front;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewUserDto {
    private String name;
    private String login;
    private String password;
    private String confirmPassword;
    private String email;
    private LocalDate birthdate;
}
