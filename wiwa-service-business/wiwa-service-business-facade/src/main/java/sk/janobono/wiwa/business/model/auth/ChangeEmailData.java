package sk.janobono.wiwa.business.model.auth;

import lombok.Builder;

@Builder
public record ChangeEmailData(String email, String password, String captchaText, String captchaToken) {
}
