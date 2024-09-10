package ru.checkdev.auth.web.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.checkdev.auth.domain.Profile;
import ru.checkdev.auth.service.TelegramService;

@RestController
@RequestMapping("/telegram")
@Slf4j
@AllArgsConstructor
public class TelegramController {

    private final TelegramService telegramService;

    @PostMapping("/bind")
    public Object bind(@RequestBody Profile profile) {
        return telegramService.bind(profile);
    }

    @PostMapping("/unbind")
    public Object unbind(@RequestBody Profile profile) {
        return telegramService.unbind(profile);
    }

    @PostMapping("/check")
    public Object check(@RequestBody Profile profile) {
        return telegramService.check(profile);
    }
}
