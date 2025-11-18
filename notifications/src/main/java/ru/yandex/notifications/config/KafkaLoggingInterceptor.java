package ru.yandex.notifications.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
@Slf4j
public class KafkaLoggingInterceptor implements ConsumerInterceptor<String, BankappMsg> {
    @Override
    public ConsumerRecords<String, BankappMsg> onConsume(ConsumerRecords<String, BankappMsg> records) {
        if (records.isEmpty()) {
            return records;
        }
        try {
            for (ConsumerRecord<String, BankappMsg> record : records) {
                Headers headers = record.headers();

                String traceId = getHeaderValue(headers, "traceId");
                String userLogin = getHeaderValue(headers, "userLogin");
                String correlationId = getHeaderValue(headers, "correlationId");

                MDC.put("traceId", traceId);
                MDC.put("userLogin", userLogin);

                log.info("Kafka-notifications onConsume: topic={}, key={}, correlationId={}", record.topic(),
                        record.key(), correlationId);
            }
        } catch (Exception e) {
            log.error("onConsume: error ", e);
        }
        return records;
    }

    @Override
    public void onCommit(Map<TopicPartition, OffsetAndMetadata> offsets) {
        log.info("Kafka-notifications onCommit: committed offsets={}", offsets);
    }

    @Override
    public void close() {
    }

    @Override
    public void configure(Map<String, ?> configs) {
    }

    private String getHeaderValue(Headers headers, String key) {
        Header header = headers.lastHeader(key);
        return (header != null && header.value() != null) ?
                new String(header.value(), StandardCharsets.UTF_8) : null;
    }
}
