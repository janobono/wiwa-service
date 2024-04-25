package sk.janobono.wiwa.api.model.order.item.part;

import sk.janobono.wiwa.model.Quantity;

public record PartCornerStraightWebDto(
        Quantity dimensionX,
        Quantity dimensionY
) implements PartCornerWebDto {
}
