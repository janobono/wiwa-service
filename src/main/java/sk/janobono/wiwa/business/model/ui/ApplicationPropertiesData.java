package sk.janobono.wiwa.business.model.ui;

public record ApplicationPropertiesData(
        String defaultLocale,
        String appTitle,
        String appDescription,
        Integer tokenExpiresIn
) {
}
