package sk.janobono.wiwa.business.model.order.part;

import sk.janobono.wiwa.business.model.DimensionsData;

import java.math.BigDecimal;

public record PartCornerRoundedData(BigDecimal radius) implements PartCornerData {
    @Override
    public DimensionsData dimensions() {
        return new DimensionsData(radius, radius);
    }
}
