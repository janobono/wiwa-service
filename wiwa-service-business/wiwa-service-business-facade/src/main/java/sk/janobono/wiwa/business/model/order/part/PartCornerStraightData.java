package sk.janobono.wiwa.business.model.order.part;

import java.math.BigDecimal;

public record PartCornerStraightData(
        BigDecimal dimensionX,
        BigDecimal dimensionY
) implements PartCornerData {
}
