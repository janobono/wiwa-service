package sk.janobono.wiwa.business.model.board;

import sk.janobono.wiwa.model.Quantity;

import java.math.BigDecimal;

public record BoardChangeData(
        String code,
        String name,
        String description,
        String boardCode,
        String structureCode,
        Boolean orientation,
        Quantity sale,
        Quantity netWeight,
        Quantity length,
        Quantity width,
        Quantity thickness,
        BigDecimal price
) {
}
