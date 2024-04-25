package sk.janobono.wiwa.dal.model;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OderItemSummaryDo(
        BigDecimal partPrice,
        BigDecimal partWeight,
        Integer amount,
        BigDecimal weight,
        BigDecimal total
) {
}
