package sk.janobono.wiwa.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sk.janobono.wiwa.business.model.captcha.CaptchaSo;
import sk.janobono.wiwa.business.service.CaptchaService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/captcha")
public class CaptchaController {

    private final CaptchaService captchaApiService;

    @GetMapping
    public ResponseEntity<CaptchaSo> getCaptcha() {
        log.debug("getCaptcha()");
        return new ResponseEntity<>(captchaApiService.getCaptcha(), HttpStatus.OK);
    }
}
