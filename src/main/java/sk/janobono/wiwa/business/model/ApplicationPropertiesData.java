package sk.janobono.wiwa.business.model;

public record ApplicationPropertiesData(
        String defaultLocale,
        String appTitle,
        String appDescription,
        Integer tokenExpiresIn
) {
}
