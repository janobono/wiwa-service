package sk.janobono.wiwa.api.model.application;

import sk.janobono.wiwa.model.Currency;

public record ApplicationPropertiesWebDto(
        String defaultLocale,
        String appTitle,
        String appDescription,
        Integer tokenExpiresIn,
        Currency currency
) {
}
