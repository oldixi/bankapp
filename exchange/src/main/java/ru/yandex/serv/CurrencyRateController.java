package ru.yandex.serv;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/currency-rate")
public class CurrencyRateController {
    private final CurrencyRateService currencyRateService;

    @GetMapping
    public List<CurrencyRate> getCurrencyRates() {
        return currencyRateService.getCurrencyRates();
    }

    @GetMapping("/exchange")
    public Double exchange(@RequestParam Double value, @RequestParam String currencyFrom, @RequestParam String currencyTo) {
        return currencyRateService.exchange(value, currencyFrom, currencyTo);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createCurrencyRate(@RequestBody CurrencyRate cur) {
        currencyRateService.save(cur);
    }
}
