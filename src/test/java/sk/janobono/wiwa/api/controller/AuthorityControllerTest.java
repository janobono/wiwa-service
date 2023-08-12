package sk.janobono.wiwa.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import sk.janobono.wiwa.BaseIntegrationTest;
import sk.janobono.wiwa.model.Authority;

import static org.assertj.core.api.Assertions.assertThat;

class AuthorityControllerTest extends BaseIntegrationTest {

    @Test
    void getAllAuthorities() {
        final String token = signIn(DEFAULT_ADMIN, PASSWORD).token();

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        final ResponseEntity<Authority[]> response = restTemplate.exchange(
                getURI("/authorities"),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Authority[].class
        );

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().length).isEqualTo(Authority.values().length);
    }
}
