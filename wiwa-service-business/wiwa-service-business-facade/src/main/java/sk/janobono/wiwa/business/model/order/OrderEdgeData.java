package sk.janobono.wiwa.business.model.order;

import lombok.Builder;
import sk.janobono.wiwa.model.Currency;
import sk.janobono.wiwa.model.Quantity;

@Builder
public record OrderEdgeData(
        Long id,
        String code,
        String name,
        Quantity weight,
        Quantity width,
        Quantity thickness,
        Currency price
) {
}
