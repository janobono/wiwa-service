package sk.janobono.wiwa.api.model.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ChangeUserDetailsWebDto(
        @Size(max = 255) String titleBefore,
        @NotBlank @Size(max = 255) String firstName,
        @Size(max = 255) String midName,
        @NotBlank @Size(max = 255) String lastName,
        @Size(max = 255) String titleAfter,
        @NotNull Boolean gdpr,
        @NotBlank String captchaText,
        @NotBlank String captchaToken
) {
}
