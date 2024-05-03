package sk.janobono.wiwa.business.model.order.summary;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderEdgeSummaryData(
        Long id,
        BigDecimal length,
        BigDecimal glueLength,
        BigDecimal weight,
        BigDecimal edgePrice,
        BigDecimal edgeVatPrice,
        BigDecimal gluePrice,
        BigDecimal glueVatPrice
) {
}
