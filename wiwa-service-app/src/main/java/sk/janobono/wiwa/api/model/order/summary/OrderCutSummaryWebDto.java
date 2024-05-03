package sk.janobono.wiwa.api.model.order.summary;

import java.math.BigDecimal;

public record OrderCutSummaryWebDto(BigDecimal thickness, BigDecimal amount, BigDecimal price, BigDecimal vatPrice) {
}
