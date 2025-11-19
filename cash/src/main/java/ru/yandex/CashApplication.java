package ru.yandex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import ru.yandex.api.AccountsClient;
import ru.yandex.config.ELKFeignInterceptor;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "ru.yandex.api", clients = {AccountsClient.class})
public class CashApplication {
    public static void main(String[] args) {
        SpringApplication.run(CashApplication.class, args);
    }

    @Bean
    public ELKFeignInterceptor feignTraceInterceptor() {
        return new ELKFeignInterceptor();
    }
}
