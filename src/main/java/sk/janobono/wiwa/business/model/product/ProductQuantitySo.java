package sk.janobono.wiwa.business.model.product;

import jakarta.validation.constraints.NotNull;
import sk.janobono.wiwa.model.ProductQuantityKey;

import java.math.BigDecimal;

public record ProductQuantitySo(
        @NotNull ProductQuantityKey key,
        @NotNull BigDecimal value,
        @NotNull Long unitId
) {
}
