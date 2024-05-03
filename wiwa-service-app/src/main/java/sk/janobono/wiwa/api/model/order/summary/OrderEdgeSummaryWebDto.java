package sk.janobono.wiwa.api.model.order.summary;

import java.math.BigDecimal;

public record OrderEdgeSummaryWebDto(
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
