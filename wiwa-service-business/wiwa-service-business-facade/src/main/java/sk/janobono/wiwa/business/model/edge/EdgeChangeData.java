package sk.janobono.wiwa.business.model.edge;

import sk.janobono.wiwa.model.Quantity;

import java.math.BigDecimal;

public record EdgeChangeData(
        String code,
        String name,
        String description,
        Quantity sale,
        Quantity netWeight,
        Quantity width,
        Quantity thickness,
        BigDecimal price
) {
}
