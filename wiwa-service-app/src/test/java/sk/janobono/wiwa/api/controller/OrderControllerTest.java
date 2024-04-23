package sk.janobono.wiwa.api.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import sk.janobono.wiwa.api.model.order.OrderCommentChangeWebDto;
import sk.janobono.wiwa.api.model.order.OrderWebDto;
import sk.janobono.wiwa.dal.repository.OrderRepository;
import sk.janobono.wiwa.model.OrderStatus;
import sk.janobono.wiwa.model.Unit;

import java.math.BigDecimal;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class OrderControllerTest extends BaseControllerTest {

    @Autowired
    public OrderRepository orderRepository;

    @Test
    void securityTest() {
        final String customerToken = signIn(DEFAULT_CUSTOMER, PASSWORD).token();
        final String employeeToken = signIn(DEFAULT_EMPLOYEE, PASSWORD).token();
        final String managerToken = signIn(DEFAULT_MANAGER, PASSWORD).token();
        final String adminToken = signIn(DEFAULT_ADMIN, PASSWORD).token();

        final HttpHeaders customerHeaders = new HttpHeaders();
        customerHeaders.setBearerAuth(customerToken);

        final HttpHeaders employeeHeaders = new HttpHeaders();
        employeeHeaders.setBearerAuth(employeeToken);

        final HttpHeaders managerHeaders = new HttpHeaders();
        managerHeaders.setBearerAuth(managerToken);

        final HttpHeaders adminHeaders = new HttpHeaders();
        adminHeaders.setBearerAuth(adminToken);

        final URI orders = getURI("/orders", prepareParams(
                List.of(100L, 110L, 120L),
                OffsetDateTime.now(),
                null,
                null,
                null,
                null,
                null,
                Pageable.unpaged()
        ));

        checkStatus(customerHeaders, orders, HttpStatus.FORBIDDEN);
        checkStatus(employeeHeaders, orders, HttpStatus.OK);
        checkStatus(managerHeaders, orders, HttpStatus.OK);
        checkStatus(adminHeaders, orders, HttpStatus.OK);

        final OrderWebDto orderWebDto = addEntity(OrderWebDto.class, adminHeaders, "/orders", "");

        final URI order = getURI("/orders/{id}", Map.of("id", orderWebDto.id().toString()));
        checkStatus(customerHeaders, order, HttpStatus.FORBIDDEN);
        checkStatus(employeeHeaders, order, HttpStatus.OK);
        checkStatus(managerHeaders, order, HttpStatus.OK);
        checkStatus(adminHeaders, order, HttpStatus.OK);

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                order,
                HttpMethod.DELETE,
                new HttpEntity<>(customerHeaders),
                JsonNode.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        response = restTemplate.exchange(
                order,
                HttpMethod.DELETE,
                new HttpEntity<>(employeeHeaders),
                JsonNode.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        response = restTemplate.exchange(
                order,
                HttpMethod.DELETE,
                new HttpEntity<>(managerHeaders),
                JsonNode.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        response = restTemplate.exchange(
                order,
                HttpMethod.DELETE,
                new HttpEntity<>(adminHeaders),
                JsonNode.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    private MultiValueMap<String, String> prepareParams(
            final List<Long> userIds,
            final OffsetDateTime createdFrom,
            final OffsetDateTime createdTo,
            final List<OrderStatus> statuses,
            final BigDecimal totalFrom,
            final BigDecimal totalTo,
            final Unit totalUnit,
            final Pageable pageable
    ) {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        addToParams(params, "userIds", Optional.ofNullable(userIds).map(v -> v.stream().map(id -> Long.toString(id)).toList()).orElse(null));
        addToParams(params, "createdFrom", createdFrom);
        addToParams(params, "createdTo", createdTo);
        addToParams(params, "statuses", Optional.ofNullable(statuses).map(v -> v.stream().map(OrderStatus::name).toList()).orElse(null));
        addToParams(params, "totalFrom", totalFrom);
        addToParams(params, "totalTo", totalTo);
        addToParams(params, "totalUnit", Optional.ofNullable(totalUnit).map(Unit::name).orElse(null));
        addPageableToParams(params, pageable);
        return params;
    }

    private JsonNode checkStatus(final HttpHeaders headers, final URI uri, final HttpStatus expectedStatus) {
        final ResponseEntity<JsonNode> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                JsonNode.class
        );
        assertThat(response.getStatusCode()).isEqualTo(expectedStatus);
        return response.getBody();
    }
}
