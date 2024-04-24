package sk.janobono.wiwa.business.model.order.part;

import sk.janobono.wiwa.model.DimensionId;
import sk.janobono.wiwa.model.Quantity;

import java.math.BigDecimal;
import java.util.Map;

public record PartChangeData(
        Map<DimensionId, BigDecimal> dimensions
) {
}
