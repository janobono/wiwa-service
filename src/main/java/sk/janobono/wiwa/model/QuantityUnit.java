package sk.janobono.wiwa.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record QuantityUnit(
        @NotEmpty String id,
        @NotNull QuantityType type,
        @NotEmpty String unit
) {
}
