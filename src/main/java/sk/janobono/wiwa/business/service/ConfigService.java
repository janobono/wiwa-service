package sk.janobono.wiwa.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.model.WiwaProperty;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class ConfigService {

    private final ApplicationPropertyService applicationPropertyService;

    public BigDecimal getVatRate() {
        return applicationPropertyService.getPropertyValue(WiwaProperty.PRODUCT_VAT_RATE)
                .map(BigDecimal::new)
                .orElse(null);
    }

    public BigDecimal setVatRate(final BigDecimal value) {
        applicationPropertyService.setApplicationProperty(WiwaProperty.PRODUCT_VAT_RATE.getGroup(),
                WiwaProperty.PRODUCT_VAT_RATE.getKey(), value.toPlainString());
        return value;
    }
}
