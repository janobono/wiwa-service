package sk.janobono.wiwa.api.model.order;

import sk.janobono.wiwa.model.OrderStatus;
import sk.janobono.wiwa.model.Unit;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record OrderWebDto(
        Long id,
        Long userId,
        ZonedDateTime created,
        OrderStatus status,
        Long orderNumber,
        String description,
        BigDecimal weightValue,
        Unit weightUnit,
        BigDecimal netWeightValue,
        Unit netWeightUnit,
        BigDecimal totalValue,
        BigDecimal vatTotalValue,
        Unit totalUnit
) {
}
