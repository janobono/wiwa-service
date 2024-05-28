package sk.janobono.wiwa.dal.model;

import lombok.Builder;
import sk.janobono.wiwa.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Builder
public record OrderViewSearchCriteriaDo(
        Set<Long> userIds,
        LocalDateTime createdFrom,
        LocalDateTime createdTo,
        LocalDate deliveryFrom,
        LocalDate deliveryTo,
        Set<OrderStatus> statuses,
        BigDecimal totalFrom,
        BigDecimal totalTo
) {
}
