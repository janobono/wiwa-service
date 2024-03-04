package sk.janobono.wiwa.api.model.product;

import jakarta.validation.constraints.NotNull;
import sk.janobono.wiwa.model.ProductQuantityKey;
import sk.janobono.wiwa.model.Unit;

import java.math.BigDecimal;

public record ProductQuantityWebDto(
        @NotNull ProductQuantityKey key,
        @NotNull BigDecimal value,
        @NotNull Unit unit
) {
}
