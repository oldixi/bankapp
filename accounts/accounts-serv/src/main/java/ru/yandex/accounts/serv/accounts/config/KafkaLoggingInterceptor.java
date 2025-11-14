package ru.yandex.accounts.serv.accounts.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.stereotype.Component;
import ru.yandex.accounts.serv.accounts.BankappMsg;

import java.util.Map;

@Component
@Slf4j
public class KafkaLoggingInterceptor implements ProducerInterceptor<String, BankappMsg> {
    @Override
    public ProducerRecord<String, BankappMsg> onSend(ProducerRecord<String, BankappMsg> record) {
        log.info("Kafka-notifications: message sended to topic={}, key={}, value={}",
                record.topic(), record.key(), record.value());
        return record;
    }

    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
        if (exception != null) {
            log.error("Kafka-notifications: error sending message to topic={}, error={}",
                    metadata.topic(), exception.getMessage());
        } else {
            log.info("Kafka-notifications: message acknowledged with topic={}, partition={}, offset={}",
                    metadata.topic(), metadata.partition(), metadata.offset());
        }
    }

    @Override
    public void close() {
    }

    @Override
    public void configure(Map<String, ?> configs) {
    }
}
