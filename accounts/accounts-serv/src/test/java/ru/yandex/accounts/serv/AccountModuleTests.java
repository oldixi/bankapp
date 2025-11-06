package ru.yandex.accounts.serv;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.accounts.dto.AccountDto;
import ru.yandex.accounts.dto.AccountTransferDto;
import ru.yandex.accounts.dto.NewAccountDto;
import ru.yandex.accounts.serv.accounts.*;
import ru.yandex.accounts.serv.api.NotificationsClient;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = AccountsApplication.class)
public class AccountModuleTests {
    @Mock
    private AccountRepository accountRepository;
    @Autowired
    private AccountMapper accountMapper;
    @Mock
    private PasswordEncoder encoder;
    @Mock
    private NotificationsClient notificationsClient;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountService = new AccountService(accountRepository, accountMapper, encoder, notificationsClient);
    }

    @Test
    void getAccount() {
        String login = "user";
        Account account = Account.builder()
                .login(login)
                .name("Вася Пупкин")
                .email("test@example.com")
                .password("user")
                .balance(1000.0)
                .build();

        when(accountRepository.findAccountByLoginIgnoreCase(anyString())).thenReturn(Optional.of(account));

        AccountDto result = accountService.getAccount(login);
        assertNotNull(result);
        assertEquals(login, result.getLogin());
        assertEquals("Вася Пупкин", result.getName());
        assertTrue(result.getErrors().isEmpty());
        verify(accountRepository).findAccountByLoginIgnoreCase(login);
    }

    @Test
    void getAccountNotExistsError() {
        String login = "notUser";
        when(accountRepository.findAccountByLoginIgnoreCase(anyString())).thenReturn(Optional.empty());

        AccountDto result = accountService.getAccount(login);
        assertNotNull(result);
        assertEquals(login, result.getLogin());
        assertFalse(result.getErrors().isEmpty());
        assertTrue(result.getErrors().contains("Лицевой счет не существует"));
    }

    @Test
    void getAccounts() {
        String login = "user";
        List<Account> accounts = List.of(
                Account.builder().login("user1").name("User One").build(),
                Account.builder().login("user2").name("User Two").build());
        List<AccountTransferDto> transfers = List.of(
                new AccountTransferDto("user1", "User One"),
                new AccountTransferDto("user2", "User Two"));

        when(accountRepository.findAccountsByLoginIsNotIgnoreCase(anyString())).thenReturn(accounts);

        List<AccountTransferDto> result = accountService.getAccounts(login);
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(accountRepository).findAccountsByLoginIsNotIgnoreCase(login);
    }

    @Test
    void saveNewBalance() {
        String login = "user";
        Account existingAccount = Account.builder()
                .login(login)
                .name("Вася Пупкин")
                .email("test@example.com")
                .balance(1000.0)
                .build();

        when(accountRepository.findAccountByLoginIgnoreCase(anyString())).thenReturn(Optional.of(existingAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(existingAccount);

        AccountDto result = accountService.saveNewBalance(login, 1500.0);
        assertNotNull(result);
        assertTrue(result.getErrors().isEmpty());
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void saveNewBalanceNegativeError() {
        String login = "user";
        Account existingAccount = Account.builder()
                .login(login)
                .name("Вася Пупкин")
                .email("test@example.com")
                .balance(1000.0)
                .build();

        when(accountRepository.findAccountByLoginIgnoreCase(anyString())).thenReturn(Optional.of(existingAccount));

        AccountDto result = accountService.saveNewBalance(login, -100.0);
        assertNotNull(result);
        assertFalse(result.getErrors().isEmpty());
        assertTrue(result.getErrors().contains("Сумма на счете не может быть отрицательной"));
        verify(accountRepository, never()).save(any());
    }

    @Test
    void deleteAccount() {
        String login = "user";
        Account existingAccount = Account.builder()
                .login(login)
                .name("Вася Пупкин")
                .email("test@example.com")
                .balance(0.0)
                .build();

        when(accountRepository.findAccountByLoginIgnoreCase(anyString())).thenReturn(Optional.of(existingAccount));

        AccountDto result = accountService.delete(login);
        assertNotNull(result);
        assertTrue(result.getErrors().isEmpty());
        verify(accountRepository).delete(any(Account.class));
    }

    @Test
    void deleteAccountNotZeroBalanceError() {
        String login = "user";
        Account existingAccount = Account.builder()
                .login(login)
                .name("Вася Пупкин")
                .email("test@example.com")
                .balance(100.0)
                .build();

        when(accountRepository.findAccountByLoginIgnoreCase(anyString())).thenReturn(Optional.of(existingAccount));

        AccountDto result = accountService.delete(login);
        assertNotNull(result);
        assertFalse(result.getErrors().isEmpty());
        assertTrue(result.getErrors().contains("Удаление счета не возможно: баланс на счете не равен 0"));
        verify(accountRepository, never()).delete(any());
    }

    @Test
    void saveNewAccount() {
        NewAccountDto newAccountDto = NewAccountDto.builder()
                .login("newuser")
                .password("password123")
                .confirmPassword("password123")
                .name("Вася Пупкин2")
                .email("new@example.com")
                .birthdate("1984-03-31")
                .build();

        Account account = Account.builder()
                .login("newuser")
                .name("Вася Пупкин2")
                .email("new@example.com")
                .password("password123")
                .birthdate(LocalDate.of(1984, 3, 31))
                .build();

        when(accountRepository.existsAccountByLoginIgnoreCase(anyString())).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        AccountDto result = accountService.saveNewAccount(newAccountDto);
        assertNotNull(result);
        assertTrue(result.getErrors().isEmpty());
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void saveNewAccountExistingLoginError() {
        NewAccountDto newAccountDto = NewAccountDto.builder()
                .login("existinguser")
                .password("password123")
                .confirmPassword("password123")
                .name("Вася Пупкин")
                .email("existing@example.com")
                .birthdate("1990-03-31")
                .build();

        when(accountRepository.existsAccountByLoginIgnoreCase(anyString())).thenReturn(true);
        AccountDto result = accountService.saveNewAccount(newAccountDto);
        assertNotNull(result);
        assertFalse(result.getErrors().isEmpty());
        assertTrue(result.getErrors().contains("Пользователь с логином existinguser уже существует"));
        verify(accountRepository, never()).save(any());
    }

    @Test
    void saveNewAccountUnder18Error() {
        NewAccountDto newAccountDto = NewAccountDto.builder()
                .login("younguser")
                .password("password123")
                .confirmPassword("password123")
                .name("Вася Пупкин Jn")
                .email("young@example.com")
                .birthdate("2020-03-31")
                .build();

        when(accountRepository.existsAccountByLoginIgnoreCase(anyString())).thenReturn(false);

        AccountDto result = accountService.saveNewAccount(newAccountDto);
        assertNotNull(result);
        assertFalse(result.getErrors().isEmpty());
        assertTrue(result.getErrors().contains("Невозможно зарегистрировать пользователя младше 18 лет"));
    }

    @Test
    void saveNewAccountPasswordsNotMatchError() {
        NewAccountDto newAccountDto = NewAccountDto.builder()
                .login("user")
                .password("password123")
                .confirmPassword("differentpassword")
                .name("Вася Пупкин")
                .email("test@example.com")
                .birthdate("1984-03-31")
                .build();

        when(accountRepository.existsAccountByLoginIgnoreCase(anyString())).thenReturn(false);

        AccountDto result = accountService.saveNewAccount(newAccountDto);
        assertNotNull(result);
        assertFalse(result.getErrors().isEmpty());
        assertTrue(result.getErrors().contains("Пароли не совпадают"));
    }

    @Test
    void changePassword() {
        String login = "user";
        String newPassword = "newpassword123";
        String confirmPassword = "newpassword123";

        Account existingAccount = Account.builder()
                .login(login)
                .name("Вася Пупкин")
                .email("test@example.com")
                .build();
        AccountDto accountDto = new AccountDto();
        accountDto.setLogin(login);
        accountDto.setName("Test User");

        when(accountRepository.findAccountByLoginIgnoreCase(anyString())).thenReturn(Optional.of(existingAccount));
        when(encoder.encode(newPassword)).thenReturn("encodedPassword");
        when(accountRepository.save(any(Account.class))).thenReturn(existingAccount);

        AccountDto result = accountService.changePassword(login, newPassword, confirmPassword);
        assertNotNull(result);
        assertTrue(result.getErrors().isEmpty());
        verify(encoder).encode(newPassword);
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void editUserInfo() {
        String login = "user";
        AccountDto inputData = new AccountDto();
        inputData.setName("Вася Пупкин2");
        inputData.setEmail("updated@example.com");
        inputData.setBirthdate(LocalDate.of(1985, 5, 15));

        Account existingAccount = Account.builder()
                .login(login)
                .name("Вася Пупкин")
                .email("old@example.com")
                .birthdate(LocalDate.of(1990, 1, 1))
                .build();
        AccountDto accountDto = new AccountDto();
        accountDto.setLogin(login);
        accountDto.setName("Вася Пупкин");
        accountDto.setEmail("old@example.com");

        when(accountRepository.findAccountByLoginIgnoreCase(anyString())).thenReturn(Optional.of(existingAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(existingAccount);

        AccountDto result = accountService.editUserInfo(login, "Вася Пупкин", "old@example.com", "1990-03-31");
        assertNotNull(result);
        assertTrue(result.getErrors().isEmpty());
        verify(accountRepository).save(any(Account.class));
    }
}
