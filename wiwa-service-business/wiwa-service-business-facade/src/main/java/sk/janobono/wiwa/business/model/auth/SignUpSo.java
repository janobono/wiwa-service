package sk.janobono.wiwa.business.model.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SignUpSo(
        @NotBlank @Size(max = 255) String username,
        @NotBlank @Size(max = 255) String password,
        @Size(max = 255) String titleBefore,
        @NotBlank @Size(max = 255) String firstName,
        @Size(max = 255) String midName,
        @NotBlank @Size(max = 255) String lastName,
        @Size(max = 255) String titleAfter,
        @NotBlank @Size(max = 255) @Email String email,
        @NotNull Boolean gdpr,
        @NotBlank String captchaText,
        @NotBlank String captchaToken
) {
    @Override
    public String toString() {
        return "SignUpSo{" +
                "username='" + username + '\'' +
                ", titleBefore='" + titleBefore + '\'' +
                ", firstName='" + firstName + '\'' +
                ", midName='" + midName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", titleAfter='" + titleAfter + '\'' +
                ", email='" + email + '\'' +
                ", gdpr=" + gdpr +
                ", captchaText='" + captchaText + '\'' +
                ", captchaToken='" + captchaToken + '\'' +
                '}';
    }
}
