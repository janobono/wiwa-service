package sk.janobono.wiwa.api.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import sk.janobono.wiwa.api.model.order.OrderWebDto;
import sk.janobono.wiwa.model.OrderStatus;
import sk.janobono.wiwa.model.Unit;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class OrderControllerTest extends BaseControllerTest {

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

        final var params = prepareParams(
                List.of(100L, 110L, 120L),
                OffsetDateTime.now(),
                null,
                null,
                null,
                null,
                null
        );

        checkStatus(customerHeaders, "/orders", params, Pageable.unpaged(), HttpStatus.FORBIDDEN);
        checkStatus(employeeHeaders, "/orders", params, Pageable.unpaged(), HttpStatus.OK);
        checkStatus(managerHeaders, "/orders", params, Pageable.unpaged(), HttpStatus.OK);
        checkStatus(adminHeaders, "/orders", params, Pageable.unpaged(), HttpStatus.OK);
    }

    private MultiValueMap<String, String> prepareParams(
            final List<Long> userIds,
            final OffsetDateTime createdFrom,
            final OffsetDateTime createdTo,
            final List<OrderStatus> statuses,
            final BigDecimal totalFrom,
            final BigDecimal totalTo,
            final Unit totalUnit
    ) {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        addToParams(params, "userIds", Optional.ofNullable(userIds).map(v -> v.stream().map(id -> Long.toString(id)).toList()).orElse(null));
        addToParams(params, "createdFrom", createdFrom);
        addToParams(params, "createdTo", createdTo);
        addToParams(params, "statuses", Optional.ofNullable(statuses).map(v -> v.stream().map(OrderStatus::name).toList()).orElse(null));
        addToParams(params, "totalFrom", totalFrom);
        addToParams(params, "totalTo", totalTo);
        addToParams(params, "totalUnit", Optional.ofNullable(totalUnit).map(Unit::name).orElse(null));
        return params;
    }

    private Page<OrderWebDto> getOrders(final HttpHeaders headers,
                                        final List<Long> userIds,
                                        final OffsetDateTime createdFrom,
                                        final OffsetDateTime createdTo,
                                        final List<OrderStatus> statuses,
                                        final BigDecimal totalFrom,
                                        final BigDecimal totalTo,
                                        final Unit totalUnit,
                                        final Pageable pageable) {
        return getEntities(OrderWebDto.class, headers, "/orders", prepareParams(
                userIds,
                createdFrom,
                createdTo,
                statuses,
                totalFrom,
                totalTo,
                totalUnit
        ), pageable);
    }

    private JsonNode checkStatus(final HttpHeaders headers, final String path, final MultiValueMap<String, String> params, final Pageable pageable, final HttpStatus expectedStatus) {
        addPageableToParams(params, pageable);
        final ResponseEntity<JsonNode> response = restTemplate.exchange(
                getURI(path, params),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                JsonNode.class
        );
        assertThat(response.getStatusCode()).isEqualTo(expectedStatus);
        return response.getBody();
    }
}
