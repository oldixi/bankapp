package ru.yandex.serv.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.serv.account.AccountService;
import ru.yandex.serv.account.AccountDto;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final UserMapper userMapper;
    private final AccountService accountService;

    public UserDto getUser(String login) {
        UserDto user = userMapper.toDto(userRepository.findUserByLoginIgnoreCase(login)
                .orElse(new User()));
        if (user.getLogin() == null)
            user.setErrors(Collections.singletonList("Пользователь с логином " + login + " не найден"));
        return user;
    }

    public UserDto save(UserDto data) {
        List<String> errors = Stream.of(checkNotNullParams(data.getName(), EUserAttributes.name),
                        checkNotNullParams(data.getLogin(), EUserAttributes.login),
                        checkNotNullParams(data.getPassword(), EUserAttributes.password),
                        checkNotNullParams(data.getConfirmPassword(), EUserAttributes.confirmPassword),
                        checkNotNullParams(data.getEmail(), EUserAttributes.email),
                        checkNotNullParams(String.valueOf(data.getBirthdate()), EUserAttributes.birthdate),
                        checkUniqueness(data.getLogin()),
                        checkPasswords(data.getPassword(), data.getConfirmPassword()),
                        checkBirthdate18(data.getBirthdate()))
                .filter(err -> !err.isBlank())
                .collect(Collectors.toList());
        data.setPassword(encoder.encode(data.getPassword()));
        UserDto user = userMapper.toDto(userRepository.save(userMapper.toEntity(data)));
        user.setErrors(errors);
        return user;
    }

    public UserDto changePassword(String login, String newPassword, String newConfirmPassword) {
        List<String> errors = Stream.of(checkNotNullParams(newPassword, EUserAttributes.password),
                        checkNotNullParams(newConfirmPassword, EUserAttributes.confirmPassword),
                        checkPasswords(newPassword, newConfirmPassword))
                .filter(err -> !err.isBlank())
                .collect(Collectors.toList());
        UserDto user = getUser(login);
        user.setPassword(encoder.encode(newPassword));
        user = userMapper.toDto(userRepository.save(userMapper.toEntity(user)));
        if (!user.getErrors().isEmpty()) errors.addAll(user.getErrors());
        user.setErrors(errors);
        return user;
    }

    @Transactional
    public UserWithAccountsDto edit(String login, UserWithAccountsDto data) {
        UserDto user = getUser(login);
        user.setLogin(data.getUser().getLogin());
        user.setName(data.getUser().getName());
        List<AccountDto> accounts = data.getAccounts();
        UserDto editedUser = save(user);
        List<AccountDto> editedAccounts = accountService.save(accounts);
        List<String> errors = editedAccounts.stream()
                .map(AccountDto::getErrors)
                .filter(accountErrors -> !accountErrors.isEmpty())
                .flatMap(List::stream)
                .collect(Collectors.toList());
        errors.addAll(editedUser.getErrors());
        return new UserWithAccountsDto(editedUser, editedAccounts, errors);
    }

    private String checkUniqueness(String login) {
        if (login != null && userRepository.existsUserByLoginIgnoreCase(login))
            return "Пользователь с логином " + login + " уже существует";
        return "";
    }

    private String checkPasswords(String password, String passwordRepeat) {
        if (password != null && passwordRepeat != null && !password.equals(passwordRepeat)) return "Пароли не совпадают";
        return "";
    }

    private String checkBirthdate18(LocalDate birthdate) {
        if (birthdate != null && birthdate.plusYears(18).isAfter(LocalDate.now()))
            return "Невозможно зарегистрировать пользователя младше 18 лет";
        return "";
    }

    private String checkNotNullParams(String attrValue, EUserAttributes attrName) {
        if (attrValue == null || attrValue.isBlank()) return "Не задан обязательный параметр " + attrName.getAttrName();
        return "";
    }
}
