package sk.janobono.wiwa.api.model.order.summary;

import java.math.BigDecimal;

public record OrderGlueSummaryWebDto(BigDecimal area, BigDecimal price, BigDecimal vatPrice) {
}
