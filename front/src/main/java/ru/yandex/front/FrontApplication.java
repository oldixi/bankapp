package ru.yandex.front;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.yandex.api.AccountsClient;
import ru.yandex.api.CashClient;
import ru.yandex.api.TransferClient;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"ru.yandex.api", "ru.yandex.front"}, 
                    clients = {AccountsClient.class, CashClient.class, TransferClient.class})
public class FrontApplication {

	public static void main(String[] args) {
		SpringApplication.run(FrontApplication.class, args);
	}

}