package ru.yandex.front;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.accounts.dto.AccountDto;
import ru.yandex.accounts.dto.AccountTransferDto;
import ru.yandex.front.front.FrontService;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class FrontApplicationTests {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private FrontService frontService;

	@Test
	void home_WhenUserNotAuthenticated_ShouldRedirectToLogin() throws Exception {
		mockMvc.perform(get("/"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login"));
	}

	@Test
	@WithMockUser(username = "user")
	void home_WhenUserAuthenticated_ShouldRedirectToUserMain() throws Exception {
		mockMvc.perform(get("/")/*.with(oidcUser())*/)
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
	@WithMockUser(username = "user")
	void userMain_WhenUserAuthenticated_ShouldReturnMainPage() throws Exception {
		// Given
		AccountDto mockAccount = AccountDto.builder()
				.login("testuser")
				.name("Иван Иванов")
				.email("test@example.com")
				.birthdate(LocalDate.of(1990, 1, 1))
				.build();

		List<AccountTransferDto> mockUsers = List.of(new AccountTransferDto("User One", "user1"),
				new AccountTransferDto("User Two", "user2"));

		when(frontService.getAccount("testuser")).thenReturn(mockAccount);
		when(frontService.getAccountsForTransfer("testuser")).thenReturn(mockUsers);

		// When & Then
		mockMvc.perform(get("/user/main")/*.with(oidcUser())*/)
				.andExpect(status().isOk())
				.andExpect(view().name("main"))
				.andExpect(model().attributeExists("login", "name", "email", "birthdate", "users"))
				.andExpect(model().attribute("login", "testuser"))
				.andExpect(model().attribute("name", "Иван Иванов"))
				.andExpect(model().attribute("email", "test@example.com"));
	}

	@Test
	void signupPage_ShouldReturnSignupForm() throws Exception {
		mockMvc.perform(get("/signup"))
				.andExpect(status().isOk())
				.andExpect(view().name("signup"))
				.andExpect(model().attributeExists("login", "name", "email", "birthdate", "errors"));
	}

	@Test
	void signup_WithValidData_ShouldRedirectToLogin() throws Exception {
		// Given
		AccountDto mockAccount = AccountDto.builder()
				.login("newuser")
				.name("Петр Петров")
				.email("new@example.com")
				.birthdate(LocalDate.of(1990, 1, 1))
				.build();

		when(frontService.registerUser(
				eq("newuser"), eq("password123"), eq("password123"),
				eq("Петр Петров"), eq("new@example.com"), eq("1990-01-01")
		)).thenReturn(mockAccount);

		// When & Then
		mockMvc.perform(post("/signup")
						.param("login", "newuser")
						.param("password", "password123")
						.param("confirmPassword", "password123")
						.param("name", "Петр Петров")
						.param("email", "new@example.com")
						.param("birthdate", "1990-01-01"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login"));
	}

	@Test
	void signup_WithInvalidData_ShouldRedirectBackWithErrors() throws Exception {
		// Given
		AccountDto mockAccount = AccountDto.builder()
				.login("newuser")
				.errors(List.of("Пароли не совпадают"))
				.build();

		when(frontService.registerUser(any(), any(), any(), any(), any(), any()))
				.thenReturn(mockAccount);

		// When & Then
		mockMvc.perform(post("/signup")
						.param("login", "newuser")
						.param("password", "password123")
						.param("confirmPassword", "different")
						.param("name", "Петр Петров")
						.param("email", "new@example.com")
						.param("birthdate", "1990-01-01"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/signup"))
				.andExpect(flash().attributeExists("errors"));
	}

	@Test
	@WithMockUser(username = "user")
	void cashActions_WithPutAction_ShouldProcessDeposit() throws Exception {
		// Given
		AccountDto mockAccount = AccountDto.builder()
				.login("testuser")
				.balance(2000.0)
				.build();

		when(frontService.cash("testuser", 500.0, "PUT")).thenReturn(mockAccount);

		// When & Then
		mockMvc.perform(post("/user/testuser/cash")
						/*.with(oidcUser())*/
						.param("amount", "500.0")
						.param("action", "PUT"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/user/main"));
	}

	@Test
	@WithMockUser(username = "user")
	void cashActions_WithGetAction_ShouldProcessWithdrawal() throws Exception {
		// Given
		AccountDto mockAccount = AccountDto.builder()
				.login("testuser")
				.balance(1000.0)
				.build();

		when(frontService.cash("testuser", 500.0, "GET")).thenReturn(mockAccount);

		// When & Then
		mockMvc.perform(post("/user/testuser/cash")
						/*.with(oidcUser())*/
						.param("amount", "500.0")
						.param("action", "GET"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/user/main"));
	}

	@Test
	@WithMockUser(username = "user")
	void cashActions_WithErrors_ShouldRedirectWithFlashAttributes() throws Exception {
		// Given
		AccountDto mockAccount = AccountDto.builder()
				.login("testuser")
				.errors(List.of("Недостаточно средств на счете"))
				.build();

		when(frontService.cash("testuser", 5000.0, "GET")).thenReturn(mockAccount);

		// When & Then
		mockMvc.perform(post("/user/testuser/cash")
						/*.with(oidcUser())*/
						.param("amount", "5000.0")
						.param("action", "GET"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/user/main"))
				.andExpect(flash().attributeExists("cashErrors"));
	}

	@Test
	@WithMockUser(username = "user")
	void cashActions_WithDifferentUser_ShouldReturnError() throws Exception {
		// When & Then
		mockMvc.perform(post("/user/otheruser/cash")
						/*.with(oidcUser())*/
						.param("amount", "500.0")
						.param("action", "PUT"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/user/main"))
				.andExpect(flash().attributeExists("error"));
	}

	@Test
	@WithMockUser(username = "user")
	void transfer_WithValidData_ShouldProcessTransfer() throws Exception {
		// Given
		AccountDto mockAccount = AccountDto.builder()
				.login("testuser")
				.balance(1000.0)
				.build();

		when(frontService.transfer("testuser", 500.0, "recipientuser"))
				.thenReturn(mockAccount);

		// When & Then
		mockMvc.perform(post("/user/testuser/transfer-other")
						/*.with(oidcUser())*/
						.param("amount", "500.0")
						.param("toLogin", "recipientuser"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/user/main"));
	}

	@Test
	@WithMockUser(username = "user")
	void transfer_WithInsufficientFunds_ShouldReturnErrors() throws Exception {
		// Given
		AccountDto mockAccount = AccountDto.builder()
				.login("testuser")
				.errors(List.of("Недостаточно средств на счете"))
				.build();

		when(frontService.transfer("testuser", 5000.0, "recipientuser"))
				.thenReturn(mockAccount);

		// When & Then
		mockMvc.perform(post("/user/testuser/transfer-other")
						/*.with(oidcUser())*/
						.param("amount", "5000.0")
						.param("toLogin", "recipientuser"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/user/main"))
				.andExpect(flash().attributeExists("transferErrors"));
	}

	@Test
	@WithMockUser(username = "user")
	void changePassword_WithValidData_ShouldUpdatePassword() throws Exception {
		// Given
		AccountDto mockAccount = AccountDto.builder()
				.login("testuser")
				.build();

		when(frontService.changePassword("testuser", "newpass123", "newpass123"))
				.thenReturn(mockAccount);

		// When & Then
		mockMvc.perform(post("/user/testuser/change-password")
//						.with(oidcUser())
						.param("password", "newpass123")
						.param("confirmPassword", "newpass123"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/user/main"));
	}

	@Test
	@WithMockUser(username = "user")
	void changePassword_WithMismatchedPasswords_ShouldReturnErrors() throws Exception {
		// Given
		AccountDto mockAccount = AccountDto.builder()
				.login("testuser")
				.errors(List.of("Пароли не совпадают"))
				.build();

		when(frontService.changePassword("testuser", "newpass123", "different"))
				.thenReturn(mockAccount);

		// When & Then
		mockMvc.perform(post("/user/testuser/change-password")
						/*.with(oidcUser())*/
						.param("password", "newpass123")
						.param("confirmPassword", "different"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/user/main"))
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
