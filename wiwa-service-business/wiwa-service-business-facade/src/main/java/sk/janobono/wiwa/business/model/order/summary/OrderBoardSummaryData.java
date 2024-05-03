package sk.janobono.wiwa.business.model.order.summary;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderBoardSummaryData(
        Long id,
        BigDecimal area,
        BigDecimal boardsCount,
        BigDecimal weight,
        BigDecimal price,
        BigDecimal vatPrice
) {
}
