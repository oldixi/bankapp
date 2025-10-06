package ru.yandex.notifications;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/email")
@AllArgsConstructor
public class EmailController {
    private final EmailNotificationService emailNotificationService;

    @PostMapping(value = "/{email}/send")
    public void sendSimpleEmail(@PathVariable String email, @RequestBody String message) {
        try {
            emailNotificationService.sendSimpleEmail(email, "BANKAPP", message);
        } catch (MailException mailException) {
            log.error("Error while sending out email {}", mailException.getMessage());
        }
    }
}
