package sk.janobono.wiwa.api.model.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignInWebDto(@NotBlank @Size(max = 255) String username, @NotBlank @Size(max = 255) String password) {
    @Override
    public String toString() {
        return "SignInData{" +
                "username='" + username + '\'' +
                '}';
    }
}
