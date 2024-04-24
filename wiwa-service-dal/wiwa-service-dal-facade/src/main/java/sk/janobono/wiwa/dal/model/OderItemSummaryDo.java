package sk.janobono.wiwa.dal.model;

import java.math.BigDecimal;

public record OderItemSummaryDo(
        BigDecimal partPrice,
        BigDecimal partNetWeight,
        BigDecimal amount,
        BigDecimal netWeight,
        BigDecimal total
) {
}
