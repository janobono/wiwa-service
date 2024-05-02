package sk.janobono.wiwa.business.impl.component;

import sk.janobono.wiwa.business.model.DimensionsData;
import sk.janobono.wiwa.business.model.order.part.PartCornerData;
import sk.janobono.wiwa.business.model.order.part.PartCornerRoundedData;
import sk.janobono.wiwa.business.model.order.part.PartCornerStraightData;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public abstract class BaseCalculationUtil {

    protected static final short PRECISION = 3;

    protected BigDecimal millimeterToMeter(final BigDecimal value) {
        return value.divide(BigDecimal.valueOf(1000), PRECISION, RoundingMode.HALF_UP);
    }

    protected BigDecimal calculateCornerLength(final PartCornerData partCorner) {
        return switch (partCorner) {
            case final PartCornerStraightData partCornerStraight -> partCornerStraight.dimensions().x().pow(2)
                    .add(partCornerStraight.dimensions().y().pow(2))
                    .sqrt(new MathContext(PRECISION, RoundingMode.HALF_UP));
            case final PartCornerRoundedData partCornerRounded -> partCornerRounded.radius()
                    .multiply(BigDecimal.TWO)
                    .multiply(new BigDecimal(Math.PI))
                    .divide(BigDecimal.valueOf(4L), new MathContext(PRECISION, RoundingMode.HALF_UP));
            default -> throw new IllegalStateException("Unexpected value: " + partCorner);
        };
    }

    protected BigDecimal calculateArea(final DimensionsData dimensions) {
        return millimeterToMeter(dimensions.x())
                .multiply(millimeterToMeter(dimensions.y()))
                .setScale(PRECISION, RoundingMode.HALF_UP);
    }

    protected BigDecimal calculatePerimeter(final DimensionsData dimensions) {
        return millimeterToMeter(dimensions.x())
                .add(millimeterToMeter(dimensions.y()))
                .multiply(BigDecimal.TWO)
                .setScale(PRECISION, RoundingMode.HALF_UP);
    }
}
