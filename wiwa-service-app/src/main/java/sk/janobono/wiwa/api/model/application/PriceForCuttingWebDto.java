package sk.janobono.wiwa.api.model.application;

import jakarta.validation.constraints.NotNull;
import sk.janobono.wiwa.model.Money;
import sk.janobono.wiwa.model.Quantity;

public record PriceForCuttingWebDto(
        @NotNull Quantity thickness,
        @NotNull Quantity sale,
        @NotNull Money price
) {
}
