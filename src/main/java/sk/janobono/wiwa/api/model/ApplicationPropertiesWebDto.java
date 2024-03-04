package sk.janobono.wiwa.api.model;

public record ApplicationPropertiesWebDto(
        String defaultLocale,
        String appTitle,
        String appDescription,
        Integer tokenExpiresIn
) {
}
