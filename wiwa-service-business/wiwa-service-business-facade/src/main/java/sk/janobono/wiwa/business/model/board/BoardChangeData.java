package sk.janobono.wiwa.business.model.board;

import sk.janobono.wiwa.model.Money;
import sk.janobono.wiwa.model.Quantity;

public record BoardChangeData(
        String code,
        String name,
        String description,
        String boardCode,
        String structureCode,
        Boolean orientation,
        Quantity sale,
        Quantity weight,
        Quantity netWeight,
        Quantity length,
        Quantity width,
        Quantity thickness,
        Money price
) {
}
