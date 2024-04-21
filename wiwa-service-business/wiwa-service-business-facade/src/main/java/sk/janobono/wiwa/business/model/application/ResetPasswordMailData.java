package sk.janobono.wiwa.business.model.application;

public record ResetPasswordMailData(String subject, String title, String message, String passwordMessage, String link) {
}
