package sk.janobono.wiwa.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import sk.janobono.wiwa.BaseTest;

import java.net.URI;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityConfigPropertiesTest extends BaseTest {

    @Test
    void fullTest() {
        HttpStatusCode httpStatusCode = getStatus(getURI("/livez"));
        assertThat(httpStatusCode).isEqualTo(HttpStatus.OK);

        httpStatusCode = getStatus(getURI("/readyz"));
        assertThat(httpStatusCode).isEqualTo(HttpStatus.OK);

        httpStatusCode = getStatus(getURI("/api-docs"));
        assertThat(httpStatusCode).isEqualTo(HttpStatus.OK);

        httpStatusCode = getStatus(getURI("/swagger-ui.html"), String.class);
        assertThat(httpStatusCode).isEqualTo(HttpStatus.OK);

        httpStatusCode = getStatus(getURI("/swagger-ui/index.html"), String.class);
        assertThat(httpStatusCode).isEqualTo(HttpStatus.OK);

        httpStatusCode = getStatus(getURI("/ui/captcha"));
        assertThat(httpStatusCode).isEqualTo(HttpStatus.OK);

        httpStatusCode = getStatus(getURI("/boards/1"));
        assertThat(httpStatusCode).isEqualTo(HttpStatus.UNAUTHORIZED);

        httpStatusCode = getStatus(getURI("/code-lists/1"));
        assertThat(httpStatusCode).isEqualTo(HttpStatus.UNAUTHORIZED);

        httpStatusCode = getStatus(getURI("/code-list-items/1"));
        assertThat(httpStatusCode).isEqualTo(HttpStatus.UNAUTHORIZED);

        httpStatusCode = getStatus(getURI("/config/vat-rate"));
        assertThat(httpStatusCode).isEqualTo(HttpStatus.UNAUTHORIZED);

        httpStatusCode = getStatus(getURI("/edges/1"));
        assertThat(httpStatusCode).isEqualTo(HttpStatus.UNAUTHORIZED);

        httpStatusCode = getStatus(getURI("/orders/1"));
        assertThat(httpStatusCode).isEqualTo(HttpStatus.UNAUTHORIZED);

        httpStatusCode = getStatus(getURI("/users/1"));
        assertThat(httpStatusCode).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    private HttpStatusCode getStatus(final URI uri) {
        return getStatus(uri, Void.class);
    }

    private <T> HttpStatusCode getStatus(final URI uri, final Class<T> bodyType) {
        final AtomicReference<HttpStatusCode> status = new AtomicReference<>();
        final ResponseEntity<T> responseEntity = restClient.get()
                .uri(uri)
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    status.set(response.getStatusCode());
                }))
                .toEntity(bodyType);
        if (status.get() != null) {
            return status.get();
        }
        return responseEntity.getStatusCode();
    }

}
