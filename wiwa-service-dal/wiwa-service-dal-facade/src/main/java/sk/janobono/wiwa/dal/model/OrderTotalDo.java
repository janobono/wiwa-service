package sk.janobono.wiwa.dal.model;

import java.math.BigDecimal;

public record OrderTotalDo(
        BigDecimal weight,
        BigDecimal total
) {
}
