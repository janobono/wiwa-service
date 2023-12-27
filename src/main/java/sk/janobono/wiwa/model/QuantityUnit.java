package sk.janobono.wiwa.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record QuantityUnit(
        Long id,
        @NotNull QuantityType type,
        @NotEmpty String name,
        @NotEmpty String unit
) {
}
