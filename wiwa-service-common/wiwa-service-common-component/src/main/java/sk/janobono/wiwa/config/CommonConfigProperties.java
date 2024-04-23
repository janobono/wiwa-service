package sk.janobono.wiwa.config;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;
import sk.janobono.wiwa.model.Currency;

@ConfigurationProperties("app.common")
@Validated
public record CommonConfigProperties(
        @NotEmpty String defaultLocale,
        @NotEmpty String appTitle,
        @NotEmpty String appDescription,
        @NotEmpty String webUrl,
        @NotEmpty String confirmPath,
        @NotEmpty String ordersPath,
        @NotEmpty @Email String mail,
        @NotEmpty @Email String ordersMail,
        @DefaultValue("1000") Integer maxImageResolution,
        @DefaultValue("130") Integer maxThumbnailResolution,
        @DefaultValue("4") @Min(4) Integer captchaLength,
        @NotNull Currency currency
) {
}
