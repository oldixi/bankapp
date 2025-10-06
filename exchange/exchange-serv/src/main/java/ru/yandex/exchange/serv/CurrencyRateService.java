package ru.yandex.exchange.serv;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.exchange.dto.ECurrency;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CurrencyRateService {
    private final CurrencyRateRepository currencyRateRepository;

    public void save(CurrencyRate cur) {
        currencyRateRepository.save(cur);
    }

    public List<CurrencyRate> getCurrencyRates() {
        return currencyRateRepository.findAll();
    }

    public CurrencyRate getCurrencyRate(String name) {
        return currencyRateRepository.getByName(name)
                .orElse(new CurrencyRate(name, ECurrency.valueOf(name).getCurrencyName(), 0));
    }

    public Double exchange(Double value, String currencyFrom, String currencyTo) {
        if (currencyFrom.equals(currencyTo)) return value;

        CurrencyRate rateFrom = getCurrencyRate(currencyFrom);
        CurrencyRate rateTo = getCurrencyRate(currencyTo);
        if (ECurrency.RUB.equals(ECurrency.valueOf(currencyFrom))) return value / rateTo.getValue();
        else if (ECurrency.RUB.equals(ECurrency.valueOf(currencyTo))) return value * rateFrom.getValue();
        else return value * rateFrom.getValue() / rateTo.getValue();
    }
}
