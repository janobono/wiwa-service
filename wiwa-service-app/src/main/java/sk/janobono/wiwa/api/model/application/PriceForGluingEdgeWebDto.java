package sk.janobono.wiwa.api.model.application;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PriceForGluingEdgeWebDto(
        @NotNull @Min(0) BigDecimal width,
        @NotNull @Min(0) BigDecimal price
) {
}
