package sk.janobono.wiwa.business.model.application;

import java.math.BigDecimal;

public record PriceForCuttingData(
        BigDecimal thickness,
        BigDecimal price
) {
}
