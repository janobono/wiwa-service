package sk.janobono.wiwa.api.model.application;

import jakarta.validation.constraints.NotBlank;

public record OrderSendMailWebDto(
        @NotBlank String subject,
        @NotBlank String title,
        @NotBlank String message,
        @NotBlank String link,
        @NotBlank String attachment
) {
}
