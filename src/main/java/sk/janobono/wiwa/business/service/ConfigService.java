package sk.janobono.wiwa.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.business.service.util.PropertyUtilService;
import sk.janobono.wiwa.model.WiwaProperty;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class ConfigService {

    private final PropertyUtilService propertyUtilService;

    public BigDecimal getVatRate() {
        return propertyUtilService.getPropertyValue(BigDecimal::new, WiwaProperty.PRODUCT_VAT_RATE)
                .orElse(null);
    }

    public BigDecimal setVatRate(final BigDecimal value) {
        propertyUtilService.setProperty(BigDecimal::toPlainString,
                WiwaProperty.PRODUCT_VAT_RATE.getGroup(), WiwaProperty.PRODUCT_VAT_RATE.getKey(), value);
        return value;
    }
}
