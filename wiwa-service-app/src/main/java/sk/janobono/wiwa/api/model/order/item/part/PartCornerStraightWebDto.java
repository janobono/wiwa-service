package sk.janobono.wiwa.api.model.order.item.part;

import java.math.BigDecimal;

public record PartCornerStraightWebDto(
        BigDecimal dimensionX,
        BigDecimal dimensionY
) implements PartCornerWebDto {
}
