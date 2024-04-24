package sk.janobono.wiwa.api.model.application;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PriceForGluingLayerWebDto(
        @NotNull @Min(0) BigDecimal price
) {
}
