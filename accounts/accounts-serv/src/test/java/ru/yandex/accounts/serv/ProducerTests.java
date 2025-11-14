package ru.yandex.accounts.serv;

import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.accounts.serv.accounts.AccountService;
import ru.yandex.accounts.serv.accounts.AccountsApplication;
import ru.yandex.accounts.serv.accounts.BankappMsg;

import java.time.Duration;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.kafka.test.hamcrest.KafkaMatchers.*;

@SpringBootTest(classes = AccountsApplication.class)
@EmbeddedKafka(topics = {"bankapp-inform"},
        partitions = 1,
        brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
@ActiveProfiles("test")
class ProducerTests {
    @Autowired
    private AccountService service;
    @Autowired
    private ConsumerFactory<String, BankappMsg> consumerFactory;

    @Test
    void shouldSendNotificationToKafkaWithCorrectKeyAndValue() {
        String topic = "bankapp-inform";
        String email = "test@yandex.ru";
        String message = "Test";
        BankappMsg expectedValue = new BankappMsg(email, message);

        service.notify(email, message);

        try (var consumer = consumerFactory.createConsumer()) {
            TopicPartition partition = new TopicPartition(topic, 0);
            consumer.assign(Collections.singletonList(partition));
            consumer.seekToBeginning(Collections.singletonList(partition));

            var consumerRecord = KafkaTestUtils.getSingleRecord(consumer, topic, Duration.ofSeconds(5));

            assertThat(consumerRecord, hasKey(email));
            assertThat(consumerRecord, hasValue(expectedValue));
            assertThat(consumerRecord, hasPartition(0));
        }
    }
}
