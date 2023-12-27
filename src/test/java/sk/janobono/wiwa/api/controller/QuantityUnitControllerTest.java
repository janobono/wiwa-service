package sk.janobono.wiwa.api.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import sk.janobono.wiwa.business.model.quantityunit.QuantityUnitDataSo;
import sk.janobono.wiwa.model.QuantityType;
import sk.janobono.wiwa.model.QuantityUnit;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class QuantityUnitControllerTest extends BaseControllerTest {

    @Test
    void fullTest() {
        final String token = signIn(DEFAULT_MANAGER, PASSWORD).token();

        final HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        final List<QuantityUnit> quantityUnits = getQuantityUnits(headers);
        assertThat(quantityUnits.size()).isEqualTo(16);

        final QuantityUnitDataSo quantityUnit = new QuantityUnitDataSo(QuantityType.PACK, "TEST", "TEST");
        final QuantityUnit newQuantityUnit = addEntity(QuantityUnit.class, headers, "/quantity-units", quantityUnit);
        assertThat(newQuantityUnit)
                .usingRecursiveComparison(RecursiveComparisonConfiguration.builder()
                        .withIgnoredFields("id").build())
                .isEqualTo(quantityUnit);

        final QuantityUnit changedQuantityUnit = setEntity(QuantityUnit.class, headers, "/quantity-units", newQuantityUnit.id(), new QuantityUnitDataSo(QuantityType.TEMPORAL, "week", "week"));
        assertThat(changedQuantityUnit.id()).isEqualTo(newQuantityUnit.id());
        assertThat(changedQuantityUnit.type()).isEqualTo(QuantityType.TEMPORAL);
        assertThat(changedQuantityUnit.name()).isEqualTo("week");
        assertThat(changedQuantityUnit.unit()).isEqualTo("week");

        deleteEntity(headers, "/quantity-units", newQuantityUnit.id());
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
}
