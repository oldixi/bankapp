package ru.yandex.accounts.dto;

import org.springframework.security.core.GrantedAuthority;

public class RoleDto implements GrantedAuthority {
    private final EUserRole role;

    public RoleDto(EUserRole role) {
        this.role = role;
    }

    @Override
    public String getAuthority() {
        return role.name();
    }
}
