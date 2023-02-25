package sk.janobono.wiwa.business.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.business.model.captcha.CaptchaSo;
import sk.janobono.wiwa.common.component.Captcha;

import java.util.Base64;

@Slf4j
@RequiredArgsConstructor
@Service
public class CaptchaServiceImpl implements CaptchaService {

    private final Captcha captcha;

    public CaptchaSo getCaptcha() {
        log.debug("getCaptcha()");

        String text = captcha.generateText();
        String image = "data:image/png;base64," + Base64.getEncoder().encodeToString(captcha.generateImage(text));
        String token = captcha.generateToken(text);

        CaptchaSo result = new CaptchaSo(token, image);
        log.debug("getCaptcha({})={}", captcha, result);
        return result;
    }
}
