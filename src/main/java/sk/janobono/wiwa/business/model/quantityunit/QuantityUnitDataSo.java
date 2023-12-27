package sk.janobono.wiwa.business.model.quantityunit;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import sk.janobono.wiwa.model.QuantityType;

public record QuantityUnitDataSo(
        @NotNull QuantityType type,
        @NotEmpty String name,
        @NotEmpty String unit
) {
}
