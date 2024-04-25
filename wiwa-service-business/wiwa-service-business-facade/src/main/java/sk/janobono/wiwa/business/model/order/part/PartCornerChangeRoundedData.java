package sk.janobono.wiwa.business.model.order.part;

import java.math.BigDecimal;

public record PartCornerChangeRoundedData(
        BigDecimal radius
) implements PartCornerChangeData {
}
