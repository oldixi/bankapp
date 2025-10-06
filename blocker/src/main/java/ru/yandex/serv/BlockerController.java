package ru.yandex.serv;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/blocker")
public class BlockerController {
    private final BlockerService blockerService;

    @GetMapping()
    public boolean isBlocked() {
        return blockerService.isBlocked();
    }
}
