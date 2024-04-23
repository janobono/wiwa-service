package sk.janobono.wiwa.api.model.application;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import sk.janobono.wiwa.api.model.QuantityWebDto;

import java.math.BigDecimal;

public record PriceForGluingLayerWebDto(
        @NotNull QuantityWebDto sale,
        @NotNull @Min(0) BigDecimal price
) {
}
