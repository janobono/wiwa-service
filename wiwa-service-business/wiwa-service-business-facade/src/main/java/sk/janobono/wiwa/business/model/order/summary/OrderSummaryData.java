package sk.janobono.wiwa.business.model.order.summary;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderSummaryData(
        BigDecimal weight,
        BigDecimal total,
        BigDecimal vatTotal
) {
}
