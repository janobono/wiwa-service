package sk.janobono.wiwa.business.impl.component;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class PriceUtil {

    public BigDecimal countVatValue(final BigDecimal value, final BigDecimal vatRate) {
        if (value == null || vatRate == null) {
            return null;
        }

        final BigDecimal multiplicand = BigDecimal.ONE.add(
                vatRate.setScale(3, RoundingMode.HALF_UP).divide(BigDecimal.valueOf(100L), RoundingMode.HALF_UP)
        );

        return value.multiply(multiplicand).setScale(3, RoundingMode.HALF_UP);
    }

    public BigDecimal countNoVatValue(final BigDecimal value, final BigDecimal vatRate) {
        if (value == null || vatRate == null) {
            return null;
        }

        final BigDecimal divider = BigDecimal.ONE.add(
                vatRate.setScale(3, RoundingMode.HALF_UP).divide(BigDecimal.valueOf(100L), RoundingMode.HALF_UP)
        );

        return value.divide(divider, RoundingMode.HALF_UP).setScale(3, RoundingMode.HALF_UP);
    }
}
