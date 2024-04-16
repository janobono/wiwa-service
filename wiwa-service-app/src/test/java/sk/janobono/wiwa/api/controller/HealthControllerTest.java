package sk.janobono.wiwa.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import sk.janobono.wiwa.api.model.HealthStatusWebDto;

import static org.assertj.core.api.Assertions.assertThat;

class HealthControllerTest extends BaseControllerTest {

    @Test
    void fullTest() {
        final var livez = restTemplate.getForEntity(getURI("/livez"), HealthStatusWebDto.class);
        assertThat(livez.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(livez.getBody()).isEqualTo(new HealthStatusWebDto("OK"));

        final var readyz = restTemplate.getForEntity(getURI("/readyz"), HealthStatusWebDto.class);
        assertThat(readyz.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(readyz.getBody()).isEqualTo(new HealthStatusWebDto("OK"));
    }
}
