package sk.janobono.wiwa.api.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import sk.janobono.wiwa.model.QuantityType;
import sk.janobono.wiwa.model.QuantityUnit;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class QuantityUnitControllerTest extends BaseControllerTest {

    @Test
    void fullTest() {
        final String token = signIn(DEFAULT_MANAGER, PASSWORD).token();

        final HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        final List<QuantityUnit> quantityUnits = getQuantityUnits(headers);
        assertThat(quantityUnits.size()).isEqualTo(16);

        final QuantityUnit quantityUnit = new QuantityUnit("TEST", QuantityType.PACK, "TEST");
        final QuantityUnit newQuantityUnit = addEntity(QuantityUnit.class, headers, "/quantity-units", quantityUnit);
        assertThat(newQuantityUnit).usingRecursiveComparison().isEqualTo(quantityUnit);

        final QuantityUnit changedQuantityUnit = setQuantityUnit(headers, new QuantityUnit(quantityUnit.id(), QuantityType.TEMPORAL, "week"));
        assertThat(changedQuantityUnit.id()).isEqualTo(quantityUnit.id());
        assertThat(changedQuantityUnit.type()).isEqualTo(QuantityType.TEMPORAL);
        assertThat(changedQuantityUnit.unit()).isEqualTo("week");

        deleteQuantityUnit(headers, quantityUnit.id());
    }

    private List<QuantityUnit> getQuantityUnits(final HttpHeaders headers) {
        final ResponseEntity<JsonNode> response = restTemplate.exchange(
                getURI("/quantity-units"),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                JsonNode.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        try {
            return getListFromNode(response.getBody(), QuantityUnit.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private QuantityUnit setQuantityUnit(final HttpHeaders headers, final QuantityUnit quantityUnit) {
        final ResponseEntity<QuantityUnit> response = restTemplate.exchange(
                getURI("/quantity-units"),
                HttpMethod.PUT,
                new HttpEntity<>(quantityUnit, headers),
                QuantityUnit.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return response.getBody();
    }

    private void deleteQuantityUnit(final HttpHeaders headers, final String id) {
        final ResponseEntity<Void> response = restTemplate.exchange(
                getURI("/quantity-units/{id}", Map.of("id", id)),
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
