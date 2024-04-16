package sk.janobono.wiwa.api.model.auth;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenWebDto(@NotBlank String token) {
}
