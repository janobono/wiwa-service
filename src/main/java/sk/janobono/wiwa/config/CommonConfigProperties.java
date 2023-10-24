package sk.janobono.wiwa.config;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("app.common")
@Validated
public record CommonConfigProperties(
        @NotEmpty String webUrl,
        @NotEmpty @Email String mail,
        @NotEmpty String initDataPath,
        @DefaultValue("1000") Integer maxImageResolution,
        @DefaultValue("130") Integer maxThumbnailResolution,
        @DefaultValue("4") @Min(4) Integer captchaLength
) {
}
