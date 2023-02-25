package sk.janobono.wiwa.business.model.auth;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenSo(@NotBlank String token) {
}
