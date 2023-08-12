package sk.janobono.wiwa.business.model.product;

import jakarta.validation.constraints.NotNull;
import sk.janobono.wiwa.model.Money;

import java.time.ZonedDateTime;

public record ProductUnitPriceSo(
        @NotNull ZonedDateTime validFrom,
        @NotNull Money unitPrice
) {
}
