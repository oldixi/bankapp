package ru.yandex.transfer.config;

import feign.Client;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestFeignConfig {
    @Bean
    @Primary
    public Client feignClient() {
        return new Client.Default(null, null);
    }
}