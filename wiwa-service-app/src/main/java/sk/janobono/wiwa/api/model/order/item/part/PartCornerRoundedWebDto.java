package sk.janobono.wiwa.api.model.order.item.part;

import sk.janobono.wiwa.model.Quantity;

public record PartCornerRoundedWebDto(
        Quantity radius
) implements PartCornerWebDto {
}
