package sk.janobono.wiwa.business.model.application;

import sk.janobono.wiwa.model.Money;
import sk.janobono.wiwa.model.Quantity;

public record PriceForCuttingData(
        Quantity thickness,
        Quantity sale,
        Money price
) {
}
