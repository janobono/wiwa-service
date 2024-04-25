package sk.janobono.wiwa.api.model.order;

import sk.janobono.wiwa.model.Currency;
import sk.janobono.wiwa.model.Quantity;

public record OrderEdgeWebDto(
        Long id,
        String code,
        String name,
        Quantity weight,
        Quantity width,
        Quantity thickness,
        Currency price
) {
}
