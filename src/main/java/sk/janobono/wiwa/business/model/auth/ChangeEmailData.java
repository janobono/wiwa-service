package sk.janobono.wiwa.business.model.auth;

public record ChangeEmailData(String email, String password, String captchaText, String captchaToken) {
}
