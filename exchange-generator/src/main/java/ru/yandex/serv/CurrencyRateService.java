package ru.yandex.serv;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class CurrencyRateService {
    private final RestTemplate restTemplate;

    @Scheduled(fixedDelay = 60000)
    public void save() {
        try {
            restTemplate.postForObject("http://exchange/currency-rate",
                    new CurrencyRate(ECurrency.RUB.name(), ECurrency.RUB.getCurrencyName(), Double.parseDouble("1D")),
                    CurrencyRate.class);
            restTemplate.postForObject("http://exchange/currency-rate",
                    new CurrencyRate(ECurrency.CNY.name(), ECurrency.CNY.getCurrencyName(), Math.random() * 30),
                    CurrencyRate.class);
            restTemplate.postForObject("http://exchange/currency-rate",
                    new CurrencyRate(ECurrency.USD.name(), ECurrency.USD.getCurrencyName(), Math.random() * 100),
                    CurrencyRate.class);
        } catch (Exception ignore) {}
    }
}
