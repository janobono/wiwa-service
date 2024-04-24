package sk.janobono.wiwa.business.model.application;

import sk.janobono.wiwa.model.Currency;

public record ApplicationPropertiesData(
        String defaultLocale,
        String appTitle,
        String appDescription,
        Integer tokenExpiresIn,
        Currency currency
) {
}
