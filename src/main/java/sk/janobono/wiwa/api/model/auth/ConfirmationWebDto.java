package sk.janobono.wiwa.api.model.auth;

import jakarta.validation.constraints.NotBlank;

public record ConfirmationWebDto(@NotBlank String token) {
}
