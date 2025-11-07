package ru.yandex.transfer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.yandex.api.AccountsClient;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"ru.yandex.api", "ru.yandex.transfer"}, clients = {AccountsClient.class})
public class TransferApplication {
	public static void main(String[] args) {
		SpringApplication.run(TransferApplication.class, args);
	}
}