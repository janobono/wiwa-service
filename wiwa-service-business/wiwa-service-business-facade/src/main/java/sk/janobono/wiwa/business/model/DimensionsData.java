package sk.janobono.wiwa.business.model;

import java.math.BigDecimal;

public record DimensionsData(BigDecimal x, BigDecimal y) {

    public DimensionsData add(final BigDecimal augend) {
        return new DimensionsData(x.add(augend), y.add(augend));
    }

    public DimensionsData subtract(final BigDecimal subtrahend) {
        return new DimensionsData(x.subtract(subtrahend), y.subtract(subtrahend));
    }

    public DimensionsData rotate() {
        return new DimensionsData(y, x);
    }
}
