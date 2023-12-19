package sk.janobono.wiwa.business.model.product;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record ProductUnitPriceSo(
        ZonedDateTime validFrom,
        BigDecimal value,
        String unit
) {
}
