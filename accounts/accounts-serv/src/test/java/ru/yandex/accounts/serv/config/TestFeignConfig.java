package ru.yandex.accounts.serv.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import feign.Client;

@TestConfiguration
public class TestFeignConfig {
    @Bean
    @Primary
    public Client feignClient() {
        return new Client.Default(null, null);
    }
}