package ru.yandex.accounts.serv.accounts.config;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import ru.yandex.accounts.serv.accounts.BankappMsg;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
@Slf4j
public class KafkaLoggingInterceptor implements ProducerInterceptor<String, BankappMsg> {
    @Override
    public ProducerRecord<String, BankappMsg> onSend(ProducerRecord<String, BankappMsg> record) {
        try {
            String traceId = MDC.get("traceId");
            String userLogin = MDC.get("userLogin");
            String currentSpanId = MDC.get("spanId");

            if (StringUtils.isNotBlank(traceId)) {
                record.headers().add("traceId", traceId.getBytes(StandardCharsets.UTF_8));
            }
            if (StringUtils.isNotBlank(userLogin)) {
                record.headers().add("userLogin", userLogin.getBytes(StandardCharsets.UTF_8));
            }
            if (StringUtils.isNotBlank(currentSpanId)) {
                record.headers().add("parentSpanId", currentSpanId.getBytes(StandardCharsets.UTF_8));
            }
            log.info("Kafka-notifications onSend: message sended to topic={}, key={}, value={}",
                    record.topic(), record.key(), record.value());
            return record;

        } catch (Exception e) {
            log.warn("onSend: error ", e);
        }

        return record;
    }

    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
        if (exception != null) {
            log.error("Kafka-notifications onAcknowledgement: error sending message to topic={}, error={}",
                    metadata != null ? metadata.topic() : "", exception.getMessage());
        } else {
            log.info("Kafka-notifications onAcknowledgement: message acknowledged with topic={}, partition={}, offset={}",
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
