package sk.janobono.wiwa.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityConfigPropertiesTest extends BaseControllerTest {

    @Autowired
    public RestTemplateBuilder restTemplateBuilder;

    @Test
    void fullTest() {
        final RestTemplate restTemplate = restTemplateBuilder.build();

        var result = restTemplate.getForEntity(getURI("/actuator/health"), Void.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);

        result = restTemplate.getForEntity(getURI("/actuator/info"), Void.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);

        result = restTemplate.getForEntity(getURI("/api-docs"), Void.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);

        result = restTemplate.getForEntity(getURI("/swagger-ui.html"), Void.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);

        result = restTemplate.getForEntity(getURI("/swagger-ui/index.html"), Void.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
