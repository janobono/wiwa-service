package sk.janobono.wiwa.business.model.order.summary;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderGlueSummaryData(BigDecimal area, BigDecimal price, BigDecimal vatPrice) {
}
