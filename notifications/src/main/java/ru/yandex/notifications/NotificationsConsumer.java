package ru.yandex.notifications;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.mail.MailException;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import ru.yandex.notifications.config.BankappMsg;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationsConsumer {
    private final EmailNotificationService emailNotificationService;

    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 2000, multiplier = 2),
            autoCreateTopics = "true",
            include = {MailException.class, RuntimeException.class},
            dltTopicSuffix = "-dlt",
            retryTopicSuffix = "-retry")
    @KafkaListener(topics = "bankapp-inform", errorHandler = "globalErrorHandler")
    public void sendEmail(BankappMsg message, Acknowledgment acknowledgment) {
        try {
            emailNotificationService.sendSimpleEmail(message.getEmail(), "BANKAPP", message.getMessage());
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("sendEmail: failed for: {}, error: {}", message.getEmail(), e.getMessage());
            throw e;
        }
    }

    @DltHandler
    public void dltHandler(BankappMsg message,
                           @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                           @Header(KafkaHeaders.EXCEPTION_MESSAGE) String exceptionMessage,
                           Acknowledgment acknowledgment) {
        log.error("dltHandler: can't send message after all retries - email{}, topic={}, error={}",
                message.getEmail(), topic, exceptionMessage);
        acknowledgment.acknowledge();
    }
}
