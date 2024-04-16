package sk.janobono.wiwa.api.model.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordWebDto(
        @NotBlank @Size(max = 255) String oldPassword,
        @NotBlank @Size(max = 255) String newPassword,
        @NotBlank String captchaText,
        @NotBlank String captchaToken
) {

    @Override
    public String toString() {
        return "ChangePasswordData{" +
                "captchaText='" + captchaText + '\'' +
                ", captchaToken='" + captchaToken + '\'' +
                '}';
    }
}
