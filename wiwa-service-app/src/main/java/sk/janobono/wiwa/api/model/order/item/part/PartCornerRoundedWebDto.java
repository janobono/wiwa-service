package sk.janobono.wiwa.api.model.order.item.part;

import java.math.BigDecimal;

public record PartCornerRoundedWebDto(
        BigDecimal radius
) implements PartCornerWebDto {
}
