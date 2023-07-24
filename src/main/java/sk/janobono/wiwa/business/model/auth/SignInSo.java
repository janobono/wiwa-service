package sk.janobono.wiwa.business.model.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignInSo(@NotBlank @Size(max = 255) String username, @NotBlank @Size(max = 255) String password) {
    @Override
    public String toString() {
        return "SignInSo{" +
                "username='" + username + '\'' +
                '}';
    }
}
