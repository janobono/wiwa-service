package sk.janobono.wiwa.api.model.application;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordMailWebDto(
        @NotBlank String subject,
        @NotBlank String title,
        @NotBlank String message,
        @NotBlank String passwordMessage,
        @NotBlank String link
) {
}
