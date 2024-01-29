package sk.janobono.wiwa.component;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = {PriceUtil.class}
)
class PriceUtilTest {

    @Autowired
    public PriceUtil priceUtil;

    @Test
    void countVatValue_EqualsToExpectedResult() {
        assertThat(priceUtil.countVatValue(BigDecimal.valueOf(100L), BigDecimal.valueOf(20L)))
                .isEqualTo(BigDecimal.valueOf(120L).setScale(2, RoundingMode.HALF_UP));
    }
}
