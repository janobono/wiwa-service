package sk.janobono.wiwa.api.model.application;

public record ApplicationPropertiesWebDto(
        String defaultLocale,
        String appTitle,
        String appDescription,
        Integer tokenExpiresIn
) {
}
