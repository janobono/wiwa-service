package sk.janobono.wiwa.business.model.auth;

import lombok.Builder;

@Builder
public record ChangePasswordData(String oldPassword, String newPassword, String captchaText, String captchaToken) {
}
