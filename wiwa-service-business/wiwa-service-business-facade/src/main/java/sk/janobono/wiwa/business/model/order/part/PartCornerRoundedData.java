package sk.janobono.wiwa.business.model.order.part;

import java.math.BigDecimal;

public record PartCornerRoundedData(
        BigDecimal radius
) implements PartCornerData {
}
