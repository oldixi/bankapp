package ru.yandex.notifications.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;

@Configuration
@EnableKafka
@Slf4j
public class KafkaErrorHandler {
    @Bean
    public KafkaListenerErrorHandler globalErrorHandler() {
        return (message, exception) -> {
            log.error("Kafka globalErrorHandler: can't process message - payload={}, headers={}, error={}",
                    message.getPayload(), message.getHeaders(), exception.getMessage(), exception);
            return message.getPayload();
        };
    }
}
