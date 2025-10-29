package ru.yandex.accounts.dto;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class NewAccountDto implements UserDetails {
    private String name;
    private String login;
    private String password;
    private String confirmPassword;
    private String email;
    private String birthdate;
    @Builder.Default()
    private List<String> errors = new ArrayList<>();
    @Builder.Default()
    private List<RoleDto> authorities = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return login;
    }
}
