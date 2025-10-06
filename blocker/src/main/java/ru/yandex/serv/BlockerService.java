package ru.yandex.serv;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlockerService {
    public boolean isBlocked() {
        return Math.random() > 0.5;
    }
}
