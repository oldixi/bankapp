package ru.yandex.accounts.serv.accounts;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.yandex.accounts.dto.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final PasswordEncoder encoder;
    private final KafkaTemplate<String, BankappMsg> kafkaTemplate;

    private static final String USER_NOT_FOUND_MSG = "Пользователь не найден";
    private static final String NEW_BALANCE_MSG = "Баланс вашего счета в банковском приложении изменился. Новый баланс:";
    private static final String USER_DELETE_MSG = "Ваш аккаунт был удален из банковского приложения";
    private static final String USER_CREATE_MSG = "Добро пожаловать в банковское приложение! Ваш аккаунт успешно создан.";
    private static final String PASSWORD_CHANGE_MSG = "Ваш пароль в банковском приложении успешно изменен";
    private static final String USER_UPDATE_MSG = "Информация о вашем аккаунте в банковском приложении успешно изменена";
    private static final String USER_WITH_LOGIN_MSG = "Пользователь с логином ";
    private static final String ALREADY_EXIST_MSG = " уже существует";
    private static final String PASSWORDS_MISMATCH_MSG = "Пароли не совпадают";
    private static final String INCORRECT_DATE_FORMAT_MSG = "Неверный формат даты рождения";
    private static final String YOUNG_USER_MSG = "Невозможно зарегистрировать пользователя младше 18 лет";
    private static final String REQUIRED_PARAM_MSG = "Не задан обязательный параметр ";
    private static final String ACCOUNT_NOT_EXIST_MSG = "Лицевой счет не существует";
    private static final String NEGATIVE_BALANCE_MSG = "Сумма на счете не может быть отрицательной";
    private static final String DELETION_NOT_POSSIBLE_MSG = "Удаление счета не возможно: баланс на счете не равен 0";
    private static final String TOPIC = "bankapp-inform";

    public AccountDto loadUserByUsername(String login) {
        return accountMapper.toDto(accountRepository.findAccountByLoginIgnoreCase(login)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND_MSG)));
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
            notify(accountFromDb.getEmail(), NEW_BALANCE_MSG + balance);
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
            notify(accountFromDb.getEmail(), USER_DELETE_MSG);
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
            notify(user.getEmail(), USER_CREATE_MSG);
        }
        user.setErrors(errors);
        log.info("register finish user={}", user);
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
            notify(user.getEmail(), PASSWORD_CHANGE_MSG);
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
            notify(user.getEmail(), USER_UPDATE_MSG);
        }
        user.setErrors(errors);
        return user;
    }

    private String checkUniqueness(String login) {
        if (login != null && accountRepository.existsAccountByLoginIgnoreCase(login))
            return USER_WITH_LOGIN_MSG + login + ALREADY_EXIST_MSG;
        return "";
    }

    private String checkPasswords(String password, String passwordRepeat) {
        if (password != null && passwordRepeat != null && !password.equals(passwordRepeat)) return PASSWORDS_MISMATCH_MSG;
        return "";
    }

    private String checkBirthdate18(String birthdateStr) {
        LocalDate birthdate = convertBirthdate(birthdateStr);
        if (birthdate == null) return INCORRECT_DATE_FORMAT_MSG;
        if (birthdate.plusYears(18).isAfter(LocalDate.now()))
            return YOUNG_USER_MSG;
        return "";
    }

    private String checkNotNullParams(String attrValue, EUserAttributes attrName) {
        if (attrValue == null || attrValue.isBlank()) return REQUIRED_PARAM_MSG + attrName.getAttrName();
        return "";
    }

    private String checkExistence(AccountDto account) {
        if (account != null && account.getName() == null) return ACCOUNT_NOT_EXIST_MSG;
        return "";
    }

    private String checkAmountNeg(Double amount) {
        if (amount != null && amount < 0) return NEGATIVE_BALANCE_MSG;
        return "";
    }

    private String checkDeletionAbility(boolean needToDelete, Double amount) {
        if (needToDelete && !amount.equals(0D))
            return DELETION_NOT_POSSIBLE_MSG;
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

    public void notify(String email, String message) {
        try {
            kafkaTemplate.send(TOPIC, email, new BankappMsg(email, message));
        } catch (Exception e) {
            log.warn("notify: Сервис доставки сообщений временно недоступен. Не удалось отправить сообщение на email {}. Ошибка {}",
                    email, e.getMessage());
        };
    }
}
