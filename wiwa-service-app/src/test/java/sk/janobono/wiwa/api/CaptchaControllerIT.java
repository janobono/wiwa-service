package sk.janobono.wiwa.api;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import sk.janobono.wiwa.BaseIntegrationTest;
import sk.janobono.wiwa.business.model.captcha.CaptchaSo;

import static org.assertj.core.api.Assertions.assertThat;

class CaptchaControllerIT extends BaseIntegrationTest {

    @Test
    void captcha() {
        ResponseEntity<CaptchaSo> response = restTemplate.exchange(
                getURI("/captcha"), HttpMethod.GET, HttpEntity.EMPTY, CaptchaSo.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }
}
