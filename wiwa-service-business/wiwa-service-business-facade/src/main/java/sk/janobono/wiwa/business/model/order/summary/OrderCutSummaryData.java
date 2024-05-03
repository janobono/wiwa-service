package sk.janobono.wiwa.business.model.order.summary;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderCutSummaryData(BigDecimal thickness, BigDecimal amount, BigDecimal price, BigDecimal vatPrice) {
}
