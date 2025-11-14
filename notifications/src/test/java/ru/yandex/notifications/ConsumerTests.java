package ru.yandex.notifications;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.notifications.config.BankappMsg;

import java.time.Duration;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@EmbeddedKafka(topics = {"bankapp-inform-test", "bankapp-inform-test-retry-0", "bankapp-inform-test-retry-1", "bankapp-inform-dlt"},
        partitions = 1,
        brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ConsumerTests {
    @Autowired
    private KafkaTemplate<String, BankappMsg> kafkaTemplate;
    @Autowired
    private ConsumerFactory<String, BankappMsg> consumerFactory;

    @Test
    void shouldConsumeMessageUsingKafkaTestUtils() {
        String topic = "bankapp-inform-test";
        BankappMsg message = new BankappMsg("test@yandex.ru", "Test");

        kafkaTemplate.send(topic, message.getEmail(), message);

        try (var consumer = consumerFactory.createConsumer()) {
            consumer.subscribe(Collections.singletonList(topic));

            var record = KafkaTestUtils.getSingleRecord(consumer, "bankapp-inform-test", Duration.ofSeconds(3));

            assertThat(record, notNullValue());
            assertThat(record.key(), equalTo("test@yandex.ru"));
            assertThat(record.value().getMessage(), equalTo("Test"));
        }
    }
}