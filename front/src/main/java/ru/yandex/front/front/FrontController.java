package ru.yandex.front.front;

import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.yandex.accounts.dto.AccountDto;
import ru.yandex.accounts.dto.NewAccountDto;
import ru.yandex.front.api.AccountsClient;

@Controller
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class FrontController {
    private final FrontService service;
    private final AccountsClient accountsClient;

    /*
    GET "/" - домашняя страница банковского приложения пользователя
    Возвращает: редирект на страницу банковского приложения /user/main
    */
    @GetMapping("/")
    public String home(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            return "redirect:/user/main";
        }
        return "redirect:/login";
    }

    /*
    GET "/user/main" - страница банковского приложения пользователя
    Возвращает: шаблон "main.html"
    */
    @GetMapping("/user/main")
    public String userMain(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        String login = userDetails.getUsername();
        AccountDto account = service.getAccount(login);

        model.addAttribute("login", login);
        model.addAttribute("name", account.getName());
        model.addAttribute("email", account.getEmail());
        model.addAttribute("birthdate", account.getBirthdate());
        model.addAttribute("users", service.getAccountsForTransfer(login));

        return "main";
    }

    /*
    GET "/login" - страница входа ы банковске приложение пользователя
    Возвращает: шаблон login
    */
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute("error", "Неверный логин и/или пароль");
        }

        if (logout != null) {
            model.addAttribute("message", "Вы успешно вышли из системы");
        }
        return "login";
    }

    /*
    GET "/signup" - страница регистрации нового пользователя
    Возвращает: шаблон "signup.html"
    */
    @GetMapping("/signup")
    public String signupPage(Model model) {
        return "signup";
    }

    /*
    POST "/signup" - регистрация пользователя
    Параметры:  "login" - логин пользователя
                "password" - пароль
                "confirmPassword" - пароль
                "email" - email
                "birthdate" - дата рождения
                "name" - ФИ пользователя
    Возвращает: редирект на форму логина "/login"
    */
/*    @PostMapping("/signup")
    public String signup(@RequestParam String login,
                         @RequestParam String password,
                         @RequestParam String confirmPassword,
                         @RequestParam String name,
                         @RequestParam String email,
                         @RequestParam String birthdate,
                         RedirectAttributes redirectAttributes) {
        AccountDto newAccount = service.registerUser(login, password, confirmPassword, name, email, birthdate);

        if (!newAccount.getErrors().isEmpty()) {
            redirectAttributes.addFlashAttribute("errors", newAccount.getErrors());
            redirectAttributes.addFlashAttribute("login", login);
            redirectAttributes.addFlashAttribute("name", name);
            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("birthdate", birthdate);
            log.info("Ошибки параметров регистрации {}", newAccount.getErrors());
            return "redirect:/signup";
        } else {
            return "redirect:/login";
        }
    }*/

    @PostMapping("/signup")
    public String signup(@RequestParam String login,
                         @RequestParam String password,
                         @RequestParam String confirmPassword,
                         @RequestParam String name,
                         @RequestParam String email,
                         @RequestParam String birthdate,
                         RedirectAttributes redirectAttributes) {
        NewAccountDto newAccountDto = NewAccountDto.builder()
                .login(login)
                .password(password)
                .confirmPassword(confirmPassword)
                .name(name)
                .email(email)
                .birthdate(birthdate)
                .build();

        try {
            log.info("Sending signup request to accounts service: {}", newAccountDto.getLogin());
            AccountDto newAccount = accountsClient.register(newAccountDto);

            if (!newAccount.getErrors().isEmpty()) {
                redirectAttributes.addFlashAttribute("errors", newAccount.getErrors());
                redirectAttributes.addFlashAttribute("login", login);
                redirectAttributes.addFlashAttribute("name", name);
                redirectAttributes.addFlashAttribute("email", email);
                redirectAttributes.addFlashAttribute("birthdate", birthdate);
                log.info("Ошибки параметров регистрации {}", newAccount.getErrors());
                return "redirect:/signup";
            } else {
                return "redirect:/login";
            }

        } catch (FeignException e) {
            log.error("Feign error during registration. Status: {}, Message: {}", e.status(), e.getMessage());
            log.error("Feign request: {}", e.request());

            if (e.status() == 403) {
                redirectAttributes.addFlashAttribute("error",
                        "Доступ запрещен. Проверьте CSRF настройки accounts сервиса.");
            } else {
                redirectAttributes.addFlashAttribute("error",
                        "Ошибка связи с сервисом accounts: " + e.getMessage());
            }
        } catch (Exception e) {
            log.error("Unexpected error during registration: ", e);
            redirectAttributes.addFlashAttribute("error", "Неожиданная ошибка: " + e.getMessage());
        }

        return "redirect:/signup";
    }

    /*
    POST "/main/{login}/change-password" - страница изменения пароля пользователя
    Параметры:  "login" - логин пользователя
                "password" - пароль
                "confirmPassword" - пароль
    Возвращает: редирект на /user/main
    */
    @PostMapping("/user/{login}/change-password")
    public String changePassword(@PathVariable String login,
                                 @RequestParam String password,
                                 @RequestParam String confirmPassword,
                                 RedirectAttributes redirectAttributes,
                                 @AuthenticationPrincipal UserDetails userDetails) {
        if (!login.equals(userDetails.getUsername())) {
            redirectAttributes.addFlashAttribute("error", "Недостаточно прав для изменения пароля");
            return "redirect:/user/main";
        }

        AccountDto account = service.changePassword(login, password, confirmPassword);
        if (!account.getErrors().isEmpty()) {
            redirectAttributes.addFlashAttribute("passwordErrors", account.getErrors());
        }

        return "redirect:/user/main";
    }

    /*
    POST "/main/{login}/edit" - страница изменения лицевых счетов пользователя
    Параметры:  "login" - логин пользователя
                "email" - email
                "birthdate" - дата рождения
                "name" - ФИ пользователя
    Возвращает: редирект на /user/main
    */
    @PostMapping("/user/{login}/edit")
    public String editUserAccount(@PathVariable String login,
                                  @RequestParam String name,
                                  @RequestParam String email,
                                  @RequestParam String birthdate,
                                  RedirectAttributes redirectAttributes,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        if (!login.equals(userDetails.getUsername())) {
            redirectAttributes.addFlashAttribute("error", "Недостаточно прав для изменения информации о пользователе");
            return "redirect:/user/main";
        }

        AccountDto account = service.editAccountInfo(login, name, email, birthdate);
        if (!account.getErrors().isEmpty()) {
            redirectAttributes.addFlashAttribute("userAccountErrors", account.getErrors());
        }

        return "redirect:/user/main";
    }

    /*
    POST "user/{login}/cash" - страница внесения/снятия средств на/с ЛС клиента
    Параметры:  "login" - логин пользователя
                "amount" - сумма средств
                "action" - тип действия (GET - снять деньги, PUT - положить деньги)
    Возвращает: редирект на /user/main
     */
    @PostMapping("/user/{login}/cash")
    public String cashActions(@PathVariable String login,
                              @RequestParam Double amount,
                              @RequestParam String action,
                              RedirectAttributes redirectAttributes,
                              @AuthenticationPrincipal UserDetails userDetails) {
        if (!login.equals(userDetails.getUsername())) {
            redirectAttributes.addFlashAttribute("error", "Недостаточно прав для выполнения операции");
            return "redirect:/user/main";
        }

        AccountDto account = service.cash(login, amount, action);
        if (!account.getErrors().isEmpty()) {
            redirectAttributes.addFlashAttribute("cashErrors", account.getErrors());
        }

        return "redirect:/user/main";
    }

    /*
     POST "/transfer/{login}/transfer-other" - перевод средств на лицевой счет другого пользователя
     Редирект на /user/main
     */
    @PostMapping("/user/{login}/transfer-other")
    public String transfer(@PathVariable String login,
                           @RequestParam Double amount,
                           @RequestParam String toLogin,
                           RedirectAttributes redirectAttributes,
                           @AuthenticationPrincipal UserDetails userDetails) {
        if (!login.equals(userDetails.getUsername())) {
            redirectAttributes.addFlashAttribute("error", "Недостаточно прав для выполнения перевода");
            return "redirect:/user/main";
        }

        AccountDto account = service.transfer(login, amount, toLogin);
        if (!account.getErrors().isEmpty()) {
            redirectAttributes.addFlashAttribute("transferErrors", account.getErrors());
        }

        return "redirect:/user/main";
    }

    /*
    POST "/user/{login}/delete" - удаление акканута пользователя из банковского приложения
    Возвращает: редирект на страницу выхода из приложения /logout
    */
    @PostMapping("/user/{login}/delete")
    public String delete(@PathVariable String login,
                         RedirectAttributes redirectAttributes,
                         @AuthenticationPrincipal UserDetails userDetails) {
        if (!login.equals(userDetails.getUsername())) {
            redirectAttributes.addFlashAttribute("error", "Недостаточно прав для удаления аккаунта");
            return "redirect:/user/main";
        }

        AccountDto account = service.delete(login);
        if (!account.getErrors().isEmpty()) {
            redirectAttributes.addFlashAttribute("deleteErrors", account.getErrors());
        }

        return "redirect:/logout";
    }
}
