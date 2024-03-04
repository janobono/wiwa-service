package sk.janobono.wiwa.api.model.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import sk.janobono.wiwa.model.Authority;

import java.util.List;

public record UserCreateWebDto(
        @NotBlank @Size(max = 255) String username,
        @Size(max = 255) String titleBefore,
        @NotBlank @Size(max = 255) String firstName,
        @Size(max = 255) String midName,
        @NotBlank @Size(max = 255) String lastName,
        @Size(max = 255) String titleAfter,
        @NotBlank @Size(max = 255) @Email String email,
        @NotNull Boolean gdpr,
        @NotNull Boolean confirmed,
        @NotNull Boolean enabled,
        List<Authority> authorities
) {
}
