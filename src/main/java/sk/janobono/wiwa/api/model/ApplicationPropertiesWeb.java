package sk.janobono.wiwa.api.model;

public record ApplicationPropertiesWeb(
        String defaultLocale,
        String appTitle,
        String appDescription,
        Integer tokenExpiresIn
) {
}
