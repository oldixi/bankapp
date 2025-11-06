package ru.yandex.accounts.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AccountDto {
    private String name;
    private String login;
    private String password;
    private String email;
    private LocalDate birthdate;
    @Builder.Default
    private Double balance = 0.0;
    @Builder.Default
    private List<String> errors = new ArrayList<>();
}
