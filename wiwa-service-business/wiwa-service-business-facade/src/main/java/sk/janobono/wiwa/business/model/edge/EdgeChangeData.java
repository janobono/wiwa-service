package sk.janobono.wiwa.business.model.edge;

import sk.janobono.wiwa.model.Money;
import sk.janobono.wiwa.model.Quantity;

public record EdgeChangeData(
        String code,
        String name,
        String description,
        Quantity sale,
        Quantity netWeight,
        Quantity width,
        Quantity thickness,
        Money price
) {
}
