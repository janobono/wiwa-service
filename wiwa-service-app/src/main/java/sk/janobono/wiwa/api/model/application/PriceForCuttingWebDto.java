package sk.janobono.wiwa.api.model.application;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PriceForCuttingWebDto(
        @NotNull @Min(0) BigDecimal thickness,
        @NotNull @Min(0) BigDecimal price
) {
}
