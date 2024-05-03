package sk.janobono.wiwa.api.model.order.summary;

import java.math.BigDecimal;

public record OrderItemEdgeSummaryWebDto(Long id, BigDecimal length, BigDecimal glueLength) {
}
