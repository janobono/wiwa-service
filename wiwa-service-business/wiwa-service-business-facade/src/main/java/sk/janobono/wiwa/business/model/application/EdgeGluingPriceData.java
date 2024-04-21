package sk.janobono.wiwa.business.model.application;

import sk.janobono.wiwa.model.Money;
import sk.janobono.wiwa.model.Quantity;

public record EdgeGluingPriceData(
        Quantity width,
        Quantity thickness,
        Quantity sale,
        Money price
) {
}
