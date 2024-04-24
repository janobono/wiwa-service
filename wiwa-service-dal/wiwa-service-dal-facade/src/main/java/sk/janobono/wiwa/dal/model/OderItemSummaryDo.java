package sk.janobono.wiwa.dal.model;

import java.math.BigDecimal;

public record OderItemSummaryDo(
        BigDecimal partPrice,
        BigDecimal partWeight,
        BigDecimal amount,
        BigDecimal weight,
        BigDecimal total
) {
}
