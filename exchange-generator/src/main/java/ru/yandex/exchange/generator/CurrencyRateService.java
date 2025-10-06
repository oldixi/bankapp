package ru.yandex.exchange.generator;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.yandex.exchange.dto.CurrencyRateDto;
import ru.yandex.exchange.dto.ECurrency;

@Service
@RequiredArgsConstructor
public class CurrencyRateService {
    private final RestTemplate restTemplate;

    @Scheduled(fixedDelay = 60000)
    public void save() {
        try {
            restTemplate.postForObject("http://exchange/currency-rate",
                    new CurrencyRateDto(ECurrency.RUB.name(), ECurrency.RUB.getCurrencyName(), Double.parseDouble("1D")),
                    CurrencyRateDto.class);
            restTemplate.postForObject("http://exchange/currency-rate",
                    new CurrencyRateDto(ECurrency.CNY.name(), ECurrency.CNY.getCurrencyName(), Math.random() * 30),
                    CurrencyRateDto.class);
            restTemplate.postForObject("http://exchange/currency-rate",
                    new CurrencyRateDto(ECurrency.USD.name(), ECurrency.USD.getCurrencyName(), Math.random() * 100),
                    CurrencyRateDto.class);
        } catch (Exception ignore) {}
    }
}
