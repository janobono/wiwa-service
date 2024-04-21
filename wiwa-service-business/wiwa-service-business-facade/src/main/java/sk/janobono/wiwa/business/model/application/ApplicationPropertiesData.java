package sk.janobono.wiwa.business.model.application;

public record ApplicationPropertiesData(
        String defaultLocale,
        String appTitle,
        String appDescription,
        Integer tokenExpiresIn
) {
}
