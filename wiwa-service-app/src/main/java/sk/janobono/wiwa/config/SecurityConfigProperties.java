package sk.janobono.wiwa.config;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("app.security")
@Validated
public record SecurityConfigProperties(
       @DefaultValue(DEFAULT_PUBLIC_PATH_PATTERN_REGEX) String publicPathPatternRegex
) {
    public static final String DEFAULT_PUBLIC_PATH_PATTERN_REGEX = "^(" +
            "/actuator/(health|info|metrics|metrics/.*)|" +
            "/api-docs.*|" +
            "/swagger-ui.*|" +
            "/auth/(confirm|reset-password|sign-in|sign-up|refresh)|" +
            "/captcha|" +
            "/ui/.*" +
            ")$";
}
