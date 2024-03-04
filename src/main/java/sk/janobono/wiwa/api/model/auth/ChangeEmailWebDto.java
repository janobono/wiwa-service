package sk.janobono.wiwa.api.model.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangeEmailWebDto(
        @NotBlank @Size(max = 255) @Email String email,
        @NotBlank @Size(max = 255) String password,
        @NotBlank String captchaText,
        @NotBlank String captchaToken
) {
    @Override
    public String toString() {
        return "ChangeEmailData{" +
                "email='" + email + '\'' +
                ", captchaText='" + captchaText + '\'' +
                ", captchaToken='" + captchaToken + '\'' +
                '}';
    }
}
