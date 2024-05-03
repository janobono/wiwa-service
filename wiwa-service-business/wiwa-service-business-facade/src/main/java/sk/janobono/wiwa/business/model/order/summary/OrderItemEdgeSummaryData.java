package sk.janobono.wiwa.business.model.order.summary;

import java.math.BigDecimal;

public record OrderItemEdgeSummaryData(Long id, BigDecimal length, BigDecimal glueLength) {
}
