package sk.janobono.wiwa.business.model.auth;

public record ResendConfirmationData(String captchaText, String captchaToken) {
}
