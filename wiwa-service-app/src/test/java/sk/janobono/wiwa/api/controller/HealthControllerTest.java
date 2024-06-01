package sk.janobono.wiwa.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import sk.janobono.wiwa.BaseTest;
import sk.janobono.wiwa.api.model.HealthStatusWebDto;

import static org.assertj.core.api.Assertions.assertThat;

class HealthControllerTest extends BaseTest {

    @Test
    void fullTest() {
        final ResponseEntity<HealthStatusWebDto> livez = restClient.get()
                .uri(getURI("/livez"))
                .retrieve()
                .toEntity(HealthStatusWebDto.class);
        assertThat(livez.getBody()).isEqualTo(new HealthStatusWebDto("OK"));
        assertThat(livez.getStatusCode()).isEqualTo(HttpStatus.OK);

        final ResponseEntity<HealthStatusWebDto> readyz = restClient.get()
                .uri(getURI("/readyz"))
                .retrieve()
                .toEntity(HealthStatusWebDto.class);
        assertThat(readyz.getBody()).isEqualTo(new HealthStatusWebDto("OK"));
        assertThat(readyz.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
