package ru.yandex.serv.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PasswordsNotMatch extends RuntimeException {
    public PasswordsNotMatch() {
        super("Пароли не совпадают");
    }
}
