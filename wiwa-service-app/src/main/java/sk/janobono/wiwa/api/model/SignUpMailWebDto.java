package sk.janobono.wiwa.api.model;

import jakarta.validation.constraints.NotBlank;

public record SignUpMailWebDto(
        @NotBlank String subject,
        @NotBlank String title,
        @NotBlank String message,
        @NotBlank String link
) {
}
