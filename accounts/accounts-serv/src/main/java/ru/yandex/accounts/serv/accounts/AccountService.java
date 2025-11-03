package ru.yandex.accounts.serv.accounts;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.yandex.accounts.dto.AccountDto;
import ru.yandex.accounts.dto.AccountTransferDto;
import ru.yandex.accounts.dto.EUserAttributes;
import ru.yandex.accounts.dto.NewAccountDto;
import ru.yandex.accounts.serv.api.NotificationsClient;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountService /*implements UserDetailsService*/ {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final PasswordEncoder encoder;
    private final NotificationsClient notificationsClient;

    //@Override
    public AccountDto loadUserByUsername(String login) {
/*        return accountMapper.toUserSecurityDto(accountRepository.findAccountByLoginIgnoreCase(login)
                        .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден")));*/
        return accountMapper.toDto(accountRepository.findAccountByLoginIgnoreCase(login)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден")));
    }

    public AccountDto getAccount(String login) {
        AccountDto accountFromDb = accountMapper.toDto(accountRepository.findAccountByLoginIgnoreCase(login)
                .orElse(Account.builder()
                        .login(login)
                        .build()));
        List<String> errors = Stream.of(checkExistence(accountFromDb))
                .filter(err -> !err.isBlank())
                .toList();
        accountFromDb.setErrors(errors);
        return accountFromDb;
    }

    public List<AccountTransferDto> getAccounts(String login) {
        return accountMapper.toTransferDto(accountRepository.findAccountsByLoginIsNotIgnoreCase(login));
    }

    public AccountDto saveNewBalance(String login, Double balance) {
        AccountDto accountFromDb = accountMapper.toDto(accountRepository.findAccountByLoginIgnoreCase(login)
                .orElse(Account.builder()
                        .login(login)
                        .build()));
        List<String> errors = Stream.of(checkExistence(accountFromDb), checkAmountNeg(balance))
                .filter(err -> !err.isBlank())
                .toList();
        accountFromDb.setBalance(balance);
        if (accountFromDb.getEmail() != null && errors.isEmpty()) {
            accountRepository.save(accountMapper.toEntity(accountFromDb));
            try {
                notificationsClient.sendNotification(accountFromDb.getEmail(),
                        "Баланс вашего счета в банковском приложении изменился. Новый баланс:" + balance);
            } catch (Exception e) {
                log.warn("Failed to send notification: " + e.getMessage());
            }
        }
        accountFromDb.setErrors(errors);
        return accountFromDb;
    }

    public AccountDto delete(String login) {
        AccountDto accountFromDb = accountMapper.toDto(accountRepository.findAccountByLoginIgnoreCase(login)
                .orElse(Account.builder().login(login).balance(0D).build()));
        List<String> errors = Stream.of(checkDeletionAbility(accountFromDb.getEmail() != null,
                        accountFromDb.getBalance()))
                .filter(err -> !err.isBlank())
                .toList();
        if (accountFromDb.getBalance().equals(0D)) {
            accountRepository.delete(accountMapper.toEntity(accountFromDb));

            try {
                notificationsClient.sendNotification(accountFromDb.getEmail(),
                        "Ваш аккаунт был удален из банковского приложения");
            } catch (Exception e) {
                log.warn("Failed to send notification: " + e.getMessage());
            }
        }
        else accountFromDb.setErrors(errors);
        return accountFromDb;
    }

    public AccountDto saveNewAccount(NewAccountDto data) {
        List<String> errors = Stream.of(checkNotNullParams(data.getName(), EUserAttributes.name),
                        checkNotNullParams(data.getLogin(), EUserAttributes.login),
                        checkNotNullParams(data.getPassword(), EUserAttributes.password),
                        checkNotNullParams(data.getConfirmPassword(), EUserAttributes.confirmPassword),
                        checkNotNullParams(data.getEmail(), EUserAttributes.email),
                        checkNotNullParams(data.getBirthdate(), EUserAttributes.birthdate),
                        checkUniqueness(data.getLogin()),
                        checkPasswords(data.getPassword(), data.getConfirmPassword()),
                        checkBirthdate18(data.getBirthdate()))
                .filter(err -> !err.isBlank())
                .toList();
        AccountDto user = accountMapper.toDto(data);
        if (errors.isEmpty()) {
            user.setPassword(encoder.encode(user.getPassword()));
            log.info("register user={}", user);
            user = accountMapper.toUserFrontDto(accountRepository.save(accountMapper.toEntity(user)));

            try {
                notificationsClient.sendNotification(data.getEmail(),
                        "Добро пожаловать в банковское приложение! Ваш аккаунт успешно создан.");
            } catch (Exception e) {
                log.warn("Failed to send notification: " + e.getMessage());
            }
        }
        user.setErrors(errors);
        return user;
    }

    public AccountDto changePassword(String login, String newPassword, String newConfirmPassword) {
        List<String> errors = Stream.of(checkNotNullParams(newPassword, EUserAttributes.password),
                        checkNotNullParams(newConfirmPassword, EUserAttributes.confirmPassword),
                        checkPasswords(newPassword, newConfirmPassword))
                .filter(err -> !err.isBlank())
                .collect(Collectors.toList());
        AccountDto user = getAccount(login);
        if (errors.isEmpty()) {
            user.setPassword(encoder.encode(newPassword));
            user = accountMapper.toUserFrontDto(accountRepository.save(accountMapper.toEntity(user)));
            try {
                notificationsClient.sendNotification(user.getEmail(),
                        "Ваш пароль в банковском приложении успешно изменен");
            } catch (Exception e) {
                log.warn("Failed to send notification: " + e.getMessage());
            }
        }
        user.setErrors(errors);
        return user;
    }

    public AccountDto editUserInfo(String login, String name, String email, String birthdateStr) {
        log.info("editUserInfo: login={}, name={}, email={}, birthdate={}", login, name, email, birthdateStr);
        AccountDto user = getAccount(login);
        log.info("editUserInfo: user={}", user);
        List<String> errors = Stream.of((birthdateStr != null && !birthdateStr.isBlank()) ? checkBirthdate18(birthdateStr) : "")
                .filter(err -> !err.isBlank())
                .toList();
        user.setName((name != null && !name.isBlank()) ? name : user.getName());
        user.setEmail((email != null && !email.isBlank()) ? email : user.getEmail());
        log.info("editUserInfo: setName and setEmail, user={}", user);
        LocalDate birthdate = user.getBirthdate();
        if (errors.isEmpty()) {
            if (birthdateStr != null && !birthdateStr.isBlank()) birthdate = convertBirthdate(birthdateStr);
            user.setBirthdate(birthdate);
            log.info("editUserInfo: setBirthdate, user={}", user);
            user = accountMapper.toUserFrontDto(accountRepository.save(accountMapper.toEntity(user)));
            try {
                notificationsClient.sendNotification(user.getEmail(),
                        "Информация о вашем аккаунте в банковском приложении успешно изменена");
            } catch (Exception e) {
                log.warn("Failed to send notification: " + e.getMessage());
            }
        }
        user.setErrors(errors);
        return user;
    }

    private String checkUniqueness(String login) {
        if (login != null && accountRepository.existsAccountByLoginIgnoreCase(login))
            return "Пользователь с логином " + login + " уже существует";
        return "";
    }

    private String checkPasswords(String password, String passwordRepeat) {
        if (password != null && passwordRepeat != null && !password.equals(passwordRepeat)) return "Пароли не совпадают";
        return "";
    }

    private String checkBirthdate18(String birthdateStr) {
        LocalDate birthdate = convertBirthdate(birthdateStr);
        if (birthdate == null) return "Неверный формат даты рождения";
        if (birthdate.plusYears(18).isAfter(LocalDate.now()))
            return "Невозможно зарегистрировать пользователя младше 18 лет";
        return "";
    }

    private String checkNotNullParams(String attrValue, EUserAttributes attrName) {
        if (attrValue == null || attrValue.isBlank()) return "Не задан обязательный параметр " + attrName.getAttrName();
        return "";
    }

    private String checkExistence(AccountDto account) {
        if (account != null && account.getName() == null) return "Лицевой счет не существует";
        return "";
    }

    private String checkAmountNeg(Double amount) {
        if (amount != null && amount < 0) return "Сумма на счете не может быть отрицательной";
        return "";
    }

    private String checkDeletionAbility(boolean needToDelete, Double amount) {
        if (needToDelete && !amount.equals(0D))
            return "Удаление счета не возможно: баланс на счете не равен 0";
        return "";
    }

    private LocalDate convertBirthdate(String birthdate) {
        try {
            return LocalDate.parse(birthdate);
        } catch (Exception e) {
            log.warn("convertBirthdate error: birthdate={}", birthdate);
        }
        return null;
    }
}
