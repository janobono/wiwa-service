package sk.janobono.wiwa.api;

import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import sk.janobono.wiwa.BaseIntegrationTest;
import sk.janobono.wiwa.common.model.Authority;

import static org.assertj.core.api.Assertions.assertThat;

class AuthorityControllerIT extends BaseIntegrationTest {

    @Test
    void getAllAuthorities() {
        String token = signIn(DEFAULT_ADMIN, PASSWORD).token();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        ResponseEntity<Authority[]> response = restTemplate.exchange(
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
