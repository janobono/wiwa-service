package sk.janobono.wiwa.business.model.auth;

public record ResetPasswordData(String email, String captchaText, String captchaToken) {
}
