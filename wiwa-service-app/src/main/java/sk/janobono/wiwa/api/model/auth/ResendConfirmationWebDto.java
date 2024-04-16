package sk.janobono.wiwa.api.model.auth;

import jakarta.validation.constraints.NotBlank;

public record ResendConfirmationWebDto(@NotBlank String captchaText, @NotBlank String captchaToken) {
}
