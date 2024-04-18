package sk.janobono.wiwa.api.model.order;

import sk.janobono.wiwa.model.OrderStatus;
import sk.janobono.wiwa.model.Unit;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderWebDto(
        Long id,
        OrderUserWebDto creator,
        LocalDateTime created,
        OrderStatus status,
        Long orderNumber,
        BigDecimal weightValue,
        Unit weightUnit,
        BigDecimal netWeightValue,
        Unit netWeightUnit,
        BigDecimal totalValue,
        BigDecimal vatTotalValue,
        Unit totalUnit
) {
}
