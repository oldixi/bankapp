package ru.yandex.serv;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CurrencyRateRepository extends JpaRepository<CurrencyRate, String> {
    Optional<CurrencyRate> getByName(String name);
}
