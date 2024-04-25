package sk.janobono.wiwa.dal.model;

import java.math.BigDecimal;

public record OrderSummaryDo(
        BigDecimal weight,
        BigDecimal total
) {
}
