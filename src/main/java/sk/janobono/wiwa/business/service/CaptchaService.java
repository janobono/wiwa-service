package sk.janobono.wiwa.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.business.model.captcha.CaptchaSo;
import sk.janobono.wiwa.component.Captcha;

import java.util.Base64;

@RequiredArgsConstructor
@Service
public class CaptchaService {

    private final Captcha captcha;

    public CaptchaSo getCaptcha() {
        final String text = captcha.generateText();
        final String image = "data:image/png;base64," + Base64.getEncoder().encodeToString(captcha.generateImage(text));
        final String token = captcha.generateToken(text);
        return new CaptchaSo(token, image);
    }
}
