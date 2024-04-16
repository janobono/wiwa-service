package sk.janobono.wiwa.business.model.auth;

public record ChangePasswordData(String oldPassword, String newPassword, String captchaText, String captchaToken) {
}
