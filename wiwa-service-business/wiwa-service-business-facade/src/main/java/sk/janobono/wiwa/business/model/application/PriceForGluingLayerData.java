package sk.janobono.wiwa.business.model.application;

import sk.janobono.wiwa.model.Quantity;

import java.math.BigDecimal;

public record PriceForGluingLayerData(
        Quantity sale,
        BigDecimal price
) {
}
