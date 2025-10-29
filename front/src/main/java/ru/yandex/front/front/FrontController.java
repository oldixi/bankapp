package ru.yandex.front.front;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.yandex.accounts.dto.AccountDto;

import java.time.LocalDate;

@Controller
@RequestMapping()
@RequiredArgsConstructor
@Slf4j
public class FrontController {
    private final FrontService service;

    /*
    GET "/main/login" - домашняя страница банковского приложения пользователя
    Возвращает: редирект на страницу банковского приложения /user/main
    */
    @GetMapping("/")
    public String home(@AuthenticationPrincipal OidcUser principal) {
        if (principal != null) {
            return "redirect:/user/main";
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /*
    GET "/main/login" - страница банковского приложения пользователя
    Возвращает: шаблон "main.html"
    */
    @GetMapping("/user/main")
    public String userMain(@AuthenticationPrincipal OidcUser principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        String login = principal.getPreferredUsername();
        AccountDto account = service.getAccount(login);

        model.addAttribute("login", login);
        model.addAttribute("name", account.getName());
        model.addAttribute("email", account.getEmail());
        model.addAttribute("birthdate", account.getBirthdate());
        model.addAttribute("users", service.getAccountsForTransfer(login));

        return "main";
    }

    /*
    GET "/logout" - страница выхода из банковского приложения пользователя
    Возвращает: шаблон редирект на /login
    */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        return "redirect:/login?logout";
    }

    /*
    GET "/signup" - страница регистрации нового пользователя
    Возвращает: шаблон "signup.html"
    */
    @GetMapping("/signup")
    public String signupPage(Model model) {
        model.addAttribute("login", "");
        model.addAttribute("name", "");
        model.addAttribute("email", "");
        model.addAttribute("birthdate", "");
        model.addAttribute("errors", "");
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
    @PostMapping("/signup")
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
            return "redirect:/signup";
        } else {
            return "redirect:/login";
        }
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
                                 @AuthenticationPrincipal OidcUser principal) {
        if (!login.equals(principal.getPreferredUsername())) {
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
                                  @AuthenticationPrincipal OidcUser principal) {
        if (!login.equals(principal.getPreferredUsername())) {
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
                                 @AuthenticationPrincipal OidcUser principal) {
        if (!login.equals(principal.getPreferredUsername())) {
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
                           @AuthenticationPrincipal OidcUser principal) {
        if (!login.equals(principal.getPreferredUsername())) {
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
    DELETE "/user/{login}/delete" - удаление акканута пользователя из банковского приложения
    Возвращает: редирект на страницу выхода из приложения /logout
    */
    @DeleteMapping("/user/{login}/delete")
    public String delete(@PathVariable String login,
                         RedirectAttributes redirectAttributes,
                         @AuthenticationPrincipal OidcUser principal) {
        if (!login.equals(principal.getPreferredUsername())) {
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
