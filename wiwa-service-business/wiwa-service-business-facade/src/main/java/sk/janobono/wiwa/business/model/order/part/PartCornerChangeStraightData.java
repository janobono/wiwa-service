package sk.janobono.wiwa.business.model.order.part;

import java.math.BigDecimal;

public record PartCornerChangeStraightData(
        BigDecimal dimensionX,
        BigDecimal dimensionY
) implements PartCornerChangeData {
}
