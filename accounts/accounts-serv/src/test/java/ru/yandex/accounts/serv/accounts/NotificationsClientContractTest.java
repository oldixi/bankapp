package ru.yandex.accounts.serv.accounts;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.accounts.dto.NewAccountDto;
import ru.yandex.accounts.serv.api.NotificationsClient;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = AccountsApplication.class)
@ActiveProfiles("test")
@AutoConfigureStubRunner(ids = {"ru.yandex:notifications:0.0.1-SNAPSHOT:stubs:8085"},
        stubsMode = StubRunnerProperties.StubsMode.LOCAL)
public class NotificationsClientContractTest {
    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private NotificationsClient notificationsClient;

    @BeforeEach
    void setUp() {
        accountService = new AccountService(accountRepository, accountMapper, encoder, notificationsClient);
    }

    @Test
    public void shouldSendWelcomeNotificationOnRegistration() {
        NewAccountDto newAccount = NewAccountDto.builder()
                .login("newuser")
                .name("Вася Пупкин")
                .email("newuser@example.com")
                .password("password123")
                .confirmPassword("password123")
                .birthdate("1990-01-01")
                .build();

        Account savedAccount = Account.builder()
                .login("newuser")
                .name("Вася Пупкин")
                .email("newuser@example.com")
                .password("encodedPassword")
                .birthdate(LocalDate.of(1990, 1, 1))
                .build();

        when(accountRepository.findAccountByLoginIgnoreCase("newuser")).thenReturn(Optional.empty());
        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

        assertDoesNotThrow(() -> accountService.saveNewAccount(newAccount));
    }

    @Test
    public void shouldSendPasswordChangeNotification() {
        String login = "passworduser";
        String newPassword = "newPassword123";
        String confirmPassword = "newPassword123";

        Account account = Account.builder()
                .login("passworduser")
                .name("Вася Пупкин")
                .email("passworduser@example.com")
                .password("oldEncodedPassword")
                .birthdate(LocalDate.of(1990, 1, 1))
                .build();

        when(accountRepository.findAccountByLoginIgnoreCase(login)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        assertDoesNotThrow(() -> accountService.changePassword(login, newPassword, confirmPassword));
    }

    @Test
    public void shouldSendBalanceChangeNotification() {
        String login = "balanceuser";
        Double newBalance = 2500.0;

        Account account = Account.builder()
                .login("balanceuser")
                .name("Вася Пупкин")
                .email("balanceuser@example.com")
                .password("password123")
                .balance(1000.0)
                .birthdate(LocalDate.of(1990, 1, 1))
                .build();

        Account updatedAccount = Account.builder()
                .login("balanceuser")
                .name("Вася Пупкин")
                .email("balanceuser@example.com")
                .password("password123")
                .balance(2500.0)
                .birthdate(LocalDate.of(1990, 1, 1))
                .build();

        when(accountRepository.findAccountByLoginIgnoreCase(login)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(updatedAccount);

        assertDoesNotThrow(() -> accountService.saveNewBalance(login, newBalance));
    }

    @Test
    public void shouldSendAccountDeletionNotification() {
        String login = "deleteuser";

        Account account = Account.builder()
                .login("deleteuser")
                .name("Вася Пупкин")
                .email("deleteuser@example.com")
                .password("password123")
                .birthdate(LocalDate.of(1990, 1, 1))
                .build();

        when(accountRepository.findAccountByLoginIgnoreCase(login)).thenReturn(Optional.of(account));
        doNothing().when(accountRepository).delete(any(Account.class));

        assertDoesNotThrow(() -> accountService.delete(login));
    }

    @Test
    public void shouldSendAccountUpdateNotification() {
        String login = "edituser";
        String newName = "Вася Пупкин";
        String newEmail = "edit@example.com";
        String birthdate = "1990-01-01";

        Account existingAccount = Account.builder()
                .login("edituser")
                .name("Старое Имя")
                .email("old@example.com")
                .password("password123")
                .birthdate(LocalDate.of(1990, 1, 1))
                .build();

        Account updatedAccount = Account.builder()
                .login("edituser")
                .name(newName)
                .email(newEmail)
                .password("password123")
                .birthdate(LocalDate.of(1990, 1, 1))
                .build();

        when(accountRepository.findAccountByLoginIgnoreCase(anyString())).thenReturn(Optional.of(existingAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(updatedAccount);

        assertDoesNotThrow(() -> accountService.editUserInfo(login, newName, newEmail, birthdate));
    }
}