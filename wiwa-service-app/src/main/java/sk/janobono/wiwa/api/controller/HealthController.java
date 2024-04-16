package sk.janobono.wiwa.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import sk.janobono.wiwa.api.model.HealthStatusWebDto;

@RequiredArgsConstructor
@RestController
public class HealthController {

    @GetMapping("/livez")
    public HealthStatusWebDto livez() {
        return new HealthStatusWebDto("OK");
    }

    @GetMapping("/readyz")
    public HealthStatusWebDto readyz() {
        return new HealthStatusWebDto("OK");
    }
}
