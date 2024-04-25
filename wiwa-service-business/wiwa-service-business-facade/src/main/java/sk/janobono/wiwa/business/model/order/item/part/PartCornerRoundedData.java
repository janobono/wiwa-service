package sk.janobono.wiwa.business.model.order.item.part;

import sk.janobono.wiwa.model.Quantity;

public record PartCornerRoundedData(
        Quantity radius
) implements PartCornerData {
}
