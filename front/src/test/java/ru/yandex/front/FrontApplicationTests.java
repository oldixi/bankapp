package ru.yandex.front;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.yandex.accounts.dto.AccountDto;
import ru.yandex.accounts.dto.AccountTransferDto;
import ru.yandex.accounts.dto.NewAccountDto;
import ru.yandex.api.AccountsClient;
import ru.yandex.api.CashClient;
import ru.yandex.api.TransferClient;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureStubRunner(ids = {"ru.yandex:accounts:0.0.1-SNAPSHOT:stubs:8082",
		"ru.yandex:cash:0.0.1-SNAPSHOT:stubs:8083",
		"ru.yandex:transfer:0.0.1-SNAPSHOT:stubs:8084",
		"ru.yandex:notifications:0.0.1-SNAPSHOT:stubs:8085"},
		stubsMode = StubRunnerProperties.StubsMode.LOCAL)
@AutoConfigureWireMock(port = 8086)
class FrontApplicationTests {
	@Autowired
	private WebApplicationContext webApplicationContext;
	@Autowired
	private MockMvc mockMvc;

	@Mock
	private FrontService frontService;
	@Mock
	private AccountsClient accountsClient;
	@Mock
	private CashClient cashClient;
	@Mock
	private TransferClient transferClient;

	@BeforeEach
	void setUp() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	void home_WhenUserNotAuthenticated_ShouldRedirectToLogin() throws Exception {
		mockMvc.perform(get("/"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login"));
	}

	@Test
	@WithMockUser(username = "user")
	void home_WhenUserAuthenticated_ShouldRedirectToUserMain() throws Exception {
		mockMvc.perform(get("/"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/user/main"));
	}

	@Test
	void userMain_WhenUserNotAuthenticated_ShouldRedirectToLogin() throws Exception {
		mockMvc.perform(get("/user/main"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login"));
	}

	@Test
	@WithMockUser(username = "testuser")
	void userMain_WhenUserAuthenticated_ShouldReturnMainPage() throws Exception {
		AccountDto mockAccount = AccountDto.builder()
				.login("testuser")
				.name("Вася Пупкин")
				.email("test@example.com")
				.birthdate(LocalDate.of(1990, 1, 1))
				.build();

		List<AccountTransferDto> mockUsers = List.of(new AccountTransferDto("User One", "user1"),
				new AccountTransferDto("User Two", "user2"));

		when(frontService.getAccount("testuser")).thenReturn(mockAccount);
		when(frontService.getAccountsForTransfer("testuser")).thenReturn(mockUsers);

		mockMvc.perform(get("/user/main"))
				.andExpect(status().isOk())
				.andExpect(view().name("main"))
				//.andExpect(model().attributeExists("login", "name", "email", "birthdate", "users"))
				.andExpect(model().attribute("login", "testuser"))
				.andExpect(model().attribute("name", "Вася Пупкин"))
				.andExpect(model().attribute("email", "test@example.com"));
	}

	@Test
	void signupPage_ShouldReturnSignupForm() throws Exception {
		mockMvc.perform(get("/signup"))
				.andExpect(status().isOk())
				.andExpect(view().name("signup"));
	}

	@Test
	void signup_WithValidData_ShouldRedirectToLogin() throws Exception {
		NewAccountDto newAccount = NewAccountDto.builder()
				.login("newuser")
				.name("Вася Пупкин")
				.email("new@example.com")
				.birthdate("1990-01-01")
				.build();
		AccountDto mockAccount = AccountDto.builder()
				.login("newuser")
				.name("Вася Пупкин")
				.email("new@example.com")
				.birthdate(LocalDate.of(1990, 1, 1))
				.build();

		when(accountsClient.register(newAccount)).thenReturn(mockAccount);
		when(frontService.registerUser(
				eq("newuser"), eq("password123"), eq("password123"),
				eq("Вася Пупкин"), eq("new@example.com"), eq("1990-01-01")
		)).thenReturn(mockAccount);

		mockMvc.perform(post("/signup")
						.param("login", "newuser")
						.param("password", "password123")
						.param("confirmPassword", "password123")
						.param("name", "Вася Пупкин")
						.param("email", "new@example.com")
						.param("birthdate", "1990-01-01"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login"));
	}

	@Test
	void signup_WithInvalidData_ShouldRedirectBackWithErrors() throws Exception {
		AccountDto mockAccount = AccountDto.builder()
				.login("newuser")
				.errors(List.of("Пароли не совпадают"))
				.build();

		when(frontService.registerUser(any(), any(), any(), any(), any(), any()))
				.thenReturn(mockAccount);

		mockMvc.perform(post("/signup")
						.param("login", "newuser")
						.param("password", "password123")
						.param("confirmPassword", "different")
						.param("name", "Вася Пупкин")
						.param("email", "new@example.com")
						.param("birthdate", "1990-01-01"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/signup"))
				.andExpect(flash().attributeExists("errors"));
	}

	@Test
	@WithMockUser(username = "testuser")
	void cashActions_WithPutAction_ShouldProcessDeposit() throws Exception {
		AccountDto mockAccount = AccountDto.builder()
				.login("testuser")
				.balance(2000.0)
				.build();

		when(frontService.cash("testuser", 500.0, "PUT")).thenReturn(mockAccount);

		mockMvc.perform(post("/user/testuser/cash")
						.param("amount", "500.0")
						.param("action", "PUT"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/user/main"));
	}

	@Test
	@WithMockUser(username = "testuser")
	void cashActions_WithGetAction_ShouldProcessWithdrawal() throws Exception {
		AccountDto mockAccount = AccountDto.builder()
				.login("testuser")
				.balance(1000.0)
				.build();

		when(frontService.cash("testuser", 500.0, "GET")).thenReturn(mockAccount);

		mockMvc.perform(post("/user/testuser/cash")
						.param("amount", "500.0")
						.param("action", "GET"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/user/main"));
	}

	@Test
	@WithMockUser(username = "user")
	void cashActions_WithErrors_ShouldRedirectWithFlashAttributes() throws Exception {
		AccountDto mockAccount = AccountDto.builder()
				.login("testuser")
				.errors(List.of("Недостаточно средств на счете"))
				.build();

		when(frontService.cash("testuser", 5000.0, "GET")).thenReturn(mockAccount);

		mockMvc.perform(post("/user/testuser/cash")
						.param("amount", "5000.0")
						.param("action", "GET"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/user/main"))
				.andExpect(flash().attributeExists("error"));
	}

	@Test
	@WithMockUser(username = "user")
	void cashActions_WithDifferentUser_ShouldReturnError() throws Exception {
		mockMvc.perform(post("/user/otheruser/cash")
						.param("amount", "500.0")
						.param("action", "PUT"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/user/main"))
				.andExpect(flash().attributeExists("error"));
	}

	@Test
	@WithMockUser(username = "user")
	void transfer_WithValidData_ShouldProcessTransfer() throws Exception {
		AccountDto mockAccount = AccountDto.builder()
				.login("testuser")
				.balance(1000.0)
				.build();

		when(frontService.transfer("testuser", 500.0, "recipientuser"))
				.thenReturn(mockAccount);

		mockMvc.perform(post("/user/testuser/transfer-other")
						/*.with(oidcUser())*/
						.param("amount", "500.0")
						.param("toLogin", "recipientuser"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/user/main"));
	}

	@Test
	@WithMockUser(username = "testuser")
	void transfer_WithInsufficientFunds_ShouldReturnErrors() throws Exception {
		AccountDto mockAccount = AccountDto.builder()
				.login("testuser")
				.errors(List.of("Недостаточно средств на счете"))
				.build();

		when(frontService.transfer("testuser", 5000.0, "recipientuser"))
				.thenReturn(mockAccount);

		mockMvc.perform(post("/user/testuser/transfer-other")
						.param("amount", "5000.0")
						.param("toLogin", "recipientuser"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/user/main"))
				.andExpect(flash().attributeExists("errors"));
	}

	@Test
	@WithMockUser(username = "user")
	void changePassword_WithValidData_ShouldUpdatePassword() throws Exception {
		AccountDto mockAccount = AccountDto.builder()
				.login("testuser")
				.build();

		when(frontService.changePassword("testuser", "newpass123", "newpass123"))
				.thenReturn(mockAccount);

		mockMvc.perform(post("/user/testuser/change-password")
						.param("password", "newpass123")
						.param("confirmPassword", "newpass123"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/user/main"));
	}

	@Test
	@WithMockUser(username = "user")
	void changePassword_WithMismatchedPasswords_ShouldReturnErrors() throws Exception {
		AccountDto mockAccount = AccountDto.builder()
				.login("testuser")
				.errors(List.of("Пароли не совпадают"))
				.build();

		when(frontService.changePassword("testuser", "newpass123", "different"))
				.thenReturn(mockAccount);

		mockMvc.perform(post("/user/testuser/change-password")
						.param("password", "newpass123")
						.param("confirmPassword", "different"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/user/main"))
				.andExpect(model().attribute("login", "testuser"))
				.andExpect(flash().attributeExists("passwordErrors"));
	}

	@Test
	@WithMockUser(username = "user")
	void editUserAccount_WithValidData_ShouldUpdateProfile() throws Exception {
		// Given
		AccountDto mockAccount = AccountDto.builder()
				.login("testuser")
				.name("Новое Имя")
				.email("new@example.com")
				.birthdate(LocalDate.of(1985, 5, 15))
				.build();

		when(frontService.editAccountInfo("testuser", "Новое Имя", "new@example.com", "1985-05-15"))
				.thenReturn(mockAccount);

		// When & Then
		mockMvc.perform(post("/user/testuser/edit")
						/*.with(oidcUser())*/
						.param("name", "Новое Имя")
						.param("email", "new@example.com")
						.param("birthdate", "1985-05-15"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/user/main"));
	}

	@Test
	@WithMockUser(username = "user")
	void delete_WithZeroBalance_ShouldDeleteAccount() throws Exception {
		// Given
		AccountDto mockAccount = AccountDto.builder()
				.login("testuser")
				.balance(0.0)
				.build();

		when(frontService.delete("testuser")).thenReturn(mockAccount);

		// When & Then
		mockMvc.perform(delete("/user/testuser/delete")/*.with(oidcUser())*/)
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/logout"));
	}

	@Test
	@WithMockUser(username = "user")
	void delete_WithNonZeroBalance_ShouldReturnErrors() throws Exception {
		// Given
		AccountDto mockAccount = AccountDto.builder()
				.login("testuser")
				.balance(1000.0)
				.errors(List.of("Удаление счета не возможно: баланс на счете не равен 0"))
				.build();

		when(frontService.delete("testuser")).thenReturn(mockAccount);

		// When & Then
		mockMvc.perform(delete("/user/testuser/delete")/*.with(oidcUser())*/)
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/user/main"))
				.andExpect(flash().attributeExists("deleteErrors"));
	}

	@Test
	void logout_ShouldRedirectToLoginWithLogoutParam() throws Exception {
		mockMvc.perform(get("/logout"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login?logout"));
	}
}
