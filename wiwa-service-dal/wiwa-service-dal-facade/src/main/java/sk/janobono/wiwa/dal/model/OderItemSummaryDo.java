package sk.janobono.wiwa.dal.model;

import java.math.BigDecimal;

public record OderItemSummaryDo(
        BigDecimal weight,
        BigDecimal total
) {
}
