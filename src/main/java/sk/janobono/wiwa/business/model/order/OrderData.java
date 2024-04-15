package sk.janobono.wiwa.business.model.order;

import lombok.Builder;
import sk.janobono.wiwa.model.OrderStatus;
import sk.janobono.wiwa.model.Unit;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Builder
public record OrderData(
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
