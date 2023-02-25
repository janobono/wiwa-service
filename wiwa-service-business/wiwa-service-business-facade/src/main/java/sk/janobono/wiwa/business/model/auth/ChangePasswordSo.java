package sk.janobono.wiwa.business.model.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordSo(
        @NotBlank @Size(max = 255) String oldPassword,
        @NotBlank @Size(max = 255) String newPassword,
        @NotBlank String captchaText,
        @NotBlank String captchaToken
) {

    @Override
    public String toString() {
        return "ChangePasswordSo{" +
                "captchaText='" + captchaText + '\'' +
                ", captchaToken='" + captchaToken + '\'' +
                '}';
    }
}
