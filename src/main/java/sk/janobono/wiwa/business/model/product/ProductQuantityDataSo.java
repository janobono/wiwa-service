package sk.janobono.wiwa.business.model.product;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import sk.janobono.wiwa.model.ProductQuantityKey;

import java.math.BigDecimal;

public record ProductQuantityDataSo(
        @NotNull ProductQuantityKey key,
        @NotNull BigDecimal value,
        @NotEmpty String unitId
) {
}
