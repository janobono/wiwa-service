package sk.janobono.wiwa.api.model.order.summary;

import java.math.BigDecimal;

public record OrderItemCutSummaryWebDto(BigDecimal thickness, BigDecimal amount) {
}
