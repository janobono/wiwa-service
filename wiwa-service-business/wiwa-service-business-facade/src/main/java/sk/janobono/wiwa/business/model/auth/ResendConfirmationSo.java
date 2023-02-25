package sk.janobono.wiwa.business.model.auth;

import jakarta.validation.constraints.NotBlank;

public record ResendConfirmationSo(@NotBlank String captchaText, @NotBlank String captchaToken) {
}
