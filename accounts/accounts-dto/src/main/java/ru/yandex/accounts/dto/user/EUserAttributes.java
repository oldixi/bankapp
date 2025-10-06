package ru.yandex.accounts.dto.user;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public enum EUserAttributes {
    login("Логин"),
    password("Пароль"),
    confirmPassword("Подтверждение пароля"),
    birthdate("Дата рождения"),
    email("Email"),
    name("Фамилия Имя пользователя");

    private String attrName;

    public String getAttrName() {
        return attrName;
    }
}
