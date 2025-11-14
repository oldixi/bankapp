package ru.yandex.notifications.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class KafkaLoggingInterceptor implements ConsumerInterceptor<String, BankappMsg> {
    @Override
    public ConsumerRecords<String, BankappMsg> onConsume(ConsumerRecords<String, BankappMsg> records) {
        log.info("Kafka-notifications: start");
        records.forEach(record -> {
            Headers headers = record.headers();
            String correlationId = "";
            for (Header header : headers) {
                if (header.key().equals("kafka_correlationId")) {
                    correlationId = new String(header.value());
                    break;
                }
            }
            log.info("Kafka-notifications: topic={}, key={}, correlationId={}",
                    record.topic(), record.key(), correlationId);
        });
        return records;
    }

    @Override
    public void onCommit(Map<TopicPartition, OffsetAndMetadata> offsets) {
        log.info("Kafka-notifications: committed offsets={}", offsets);
    }

    @Override
    public void close() {
    }

    @Override
    public void configure(Map<String, ?> configs) {
    }
}
