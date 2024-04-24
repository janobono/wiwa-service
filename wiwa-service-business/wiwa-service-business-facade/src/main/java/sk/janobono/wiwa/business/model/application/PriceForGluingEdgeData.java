package sk.janobono.wiwa.business.model.application;

import java.math.BigDecimal;

public record PriceForGluingEdgeData(
        BigDecimal width,
        BigDecimal price
) {
}
