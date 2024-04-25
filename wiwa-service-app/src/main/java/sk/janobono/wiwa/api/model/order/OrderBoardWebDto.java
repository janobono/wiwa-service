package sk.janobono.wiwa.api.model.order;

import sk.janobono.wiwa.model.Currency;
import sk.janobono.wiwa.model.Quantity;

public record OrderBoardWebDto(
        Long id,
        String code,
        String name,
        String boardCode,
        String structureCode,
        Boolean orientation,
        Quantity weight,
        Quantity length,
        Quantity width,
        Quantity thickness,
        Currency price
) {
}
