package ru.yandex.front;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.yandex.cash.CashRequestDto;
import ru.yandex.transfer.TransferRequestDto;
import ru.yandex.accounts.dto.user.NewPasswordDto;
import ru.yandex.accounts.dto.user.UserDto;

import java.util.List;

@Controller
@RequestMapping()
@RequiredArgsConstructor
@Slf4j
public class FrontController {
    private final FrontService service;
    /*
    GET "/main/login" - страница добавления поста
    Возвращает: шаблон "main.html"
    */
    @GetMapping("/main/{login}")
    public String mainPage(Model model, @PathVariable("login") String login) {
        return "main";
    }

    /*
    POST "/main/{login}/edit" - страница изменения лицевых счетов пользователя
    Редирект на accounts/main/{login}
    */
    @PostMapping("/main/{login}/edit")
    public String editUserAccounts(Model model, @PathVariable("login") String login, @RequestBody UserDto user) {
        model.addAttribute("login", login);
        model.addAttribute("userAccountsError", service.editAccountInfo(login, user));
        model.addAttribute("user", user);
        return "redirect:/main/" + login;
    }

    /*
    POST "/main/{login}/change-password" - страница изменения лицевых счетов пользователя
    Редирект на accounts/main/{login}
    */
    @PostMapping("/main/{login}/change-password")
    public String changePassword(Model model, @PathVariable("login") String login, @RequestBody NewPasswordDto newPass) {
        model.addAttribute("login", login);
        model.addAttribute("passwordErrors",
                service.changePassword(login, newPass.getPassword(), newPass.getConfirmPassword()));
        return "redirect:/main/" + login;
    }

    /*
    GET "/signup" - страница добавления товара
    Возвращает: шаблон "signup.html"
    */
    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    /*
    POST "/signup" - создание аккаунта
    Параметры:  "login" - логин пользователя
                "password" - пароль
                "confirmPassword" - пароль
                "email" - email
                "birthdate" - дата рождения
                "name" - ФИ пользователя
    Возвращает: редирект на форму логина "/login"
    */
    @PostMapping("/signup")
    public String registerUser(Model model,
                               @ModelAttribute("user") NewUserDto user,
                               @ModelAttribute("errors") List<String> errors) {
            model.addAttribute("errors", errors);
            if (!errors.isEmpty()) {
                user.setPassword(null);
                user.setConfirmPassword(null);
            }
            model.addAttribute("user", user);
            List<String> resultErrors = service.registerUser(user);

            if (!resultErrors.isEmpty()) return "redirect:/main/" + user.getLogin();
            return registerUser(model, user, resultErrors);
    }

    /*
     POST "/cash/{login}" - страница изменения лицевых счетов пользователя
     Редирект на accounts/main/{login}
     */
    @PostMapping("/cash/{login}")
    public String cash(Model model, @PathVariable("login") String login, @RequestBody CashRequestDto cashDto) {
        model.addAttribute("login", login);
        model.addAttribute("cashErrors",
                service.cash(login, cashDto));
        return "redirect:/main/" + login;
    }

    /*
     POST "/transfer/{login}" - страница изменения лицевых счетов пользователя
     Редирект на accounts/main/{login}
     */
    @PostMapping("/transfer/{login}")
    public String transfer(Model model, @PathVariable("login") String login, @RequestBody TransferRequestDto transferDto) {
        model.addAttribute("login", login);
        model.addAttribute("transferErrors", service.transfer(login, transferDto));
        return "redirect:/main/" + login;
    }

    /*
     POST "/transfer/{login}/other" - страница изменения лицевых счетов пользователя
     Редирект на accounts/main/{login}
     */
    @PostMapping("/transfer/{login}/other")
    public String transferOther(Model model, @PathVariable("login") String login, @RequestBody TransferRequestDto transferDto) {
        model.addAttribute("login", login);
        model.addAttribute("transferOtherErrors", service.transferOther(login, transferDto));
        return "redirect:/main/" + login;
    }
}
