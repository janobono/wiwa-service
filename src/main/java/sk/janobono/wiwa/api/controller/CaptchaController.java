package sk.janobono.wiwa.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sk.janobono.wiwa.business.model.captcha.CaptchaSo;
import sk.janobono.wiwa.business.service.CaptchaService;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/captcha")
public class CaptchaController {

    private final CaptchaService captchaService;

    @GetMapping
    public CaptchaSo getCaptcha() {
        return captchaService.getCaptcha();
    }
}
