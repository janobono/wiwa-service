package sk.janobono.wiwa.business.model.product;

import sk.janobono.wiwa.model.ProductQuantityKey;

import java.math.BigDecimal;

public record ProductQuantitySo(
        ProductQuantityKey key,
        BigDecimal value,
        String unit
) {
}
