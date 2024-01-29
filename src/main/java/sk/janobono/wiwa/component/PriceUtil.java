package sk.janobono.wiwa.component;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class PriceUtil {

    public BigDecimal countVatValue(final BigDecimal value, final BigDecimal vatRate) {
        final BigDecimal multiplicand = BigDecimal.ONE.add(
                vatRate
                        .setScale(2, RoundingMode.HALF_UP)
                        .divide(BigDecimal.valueOf(100L), RoundingMode.HALF_UP)
        );
        return value.multiply(multiplicand).setScale(2, RoundingMode.HALF_UP);
    }
}
