package ru.yandex.front.front;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.yandex.front.api.AccountsClient;
import ru.yandex.front.api.CashClient;
import ru.yandex.front.api.TransferClient;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(clients = {AccountsClient.class, CashClient.class, TransferClient.class})
public class FrontApplication {
	public static void main(String[] args) {
		SpringApplication.run(FrontApplication.class, args);
	}
}
