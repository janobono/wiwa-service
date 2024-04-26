package sk.janobono.wiwa.dal.domain;

import java.math.BigDecimal;

public record OrderSummaryViewDo(
        Long id,
        String code,
        BigDecimal amount
) {
}
