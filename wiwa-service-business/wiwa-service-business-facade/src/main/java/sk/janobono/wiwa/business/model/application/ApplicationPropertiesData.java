package sk.janobono.wiwa.business.model.application;

import lombok.Builder;
import sk.janobono.wiwa.model.Currency;

@Builder
public record ApplicationPropertiesData(
        String defaultLocale,
        String appTitle,
        String appDescription,
        Integer tokenExpiresIn,
        Currency currency
) {
}
