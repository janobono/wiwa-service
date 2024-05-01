package sk.janobono.wiwa.business.impl.component;

import java.math.BigDecimal;
import java.math.RoundingMode;

public abstract class BaseCalculationUtil {

    protected static final short PRECISION = 3;

    protected BigDecimal millimeterToMeter(final BigDecimal value) {
        return value.divide(BigDecimal.valueOf(1000), PRECISION, RoundingMode.HALF_UP);
    }
}
