package sk.janobono.wiwa.business.model.order.summary;

import java.math.BigDecimal;

public record OrderEdgeSummaryData(Long id, BigDecimal length, BigDecimal glueLength) {
}
