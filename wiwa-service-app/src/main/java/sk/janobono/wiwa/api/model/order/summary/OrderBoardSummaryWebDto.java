package sk.janobono.wiwa.api.model.order.summary;

import java.math.BigDecimal;

public record OrderBoardSummaryWebDto(
        Long id,
        BigDecimal area,
        BigDecimal boardsCount,
        BigDecimal weight,
        BigDecimal price,
        BigDecimal vatPrice
) {
}
