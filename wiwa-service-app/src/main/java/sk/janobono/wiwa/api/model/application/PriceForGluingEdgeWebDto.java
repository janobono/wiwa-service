package sk.janobono.wiwa.api.model.application;

import jakarta.validation.constraints.NotNull;
import sk.janobono.wiwa.model.Money;
import sk.janobono.wiwa.model.Quantity;

public record PriceForGluingEdgeWebDto(
        @NotNull Quantity width,
        @NotNull Quantity sale,
        @NotNull Money price
) {
}
