package ru.yandex.cash.cash;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.yandex.cash.api.AccountsClient;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(clients = {AccountsClient.class})
public class CashApplication {
    public static void main(String[] args) {
        SpringApplication.run(CashApplication.class, args);
    }
}
