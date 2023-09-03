package sk.janobono.wiwa.business.model.product;

import jakarta.validation.constraints.NotNull;
import sk.janobono.wiwa.model.Quantity;

import java.time.ZonedDateTime;

public record ProductUnitPriceSo(
        @NotNull ZonedDateTime validFrom,
        @NotNull Quantity price
) {
}
