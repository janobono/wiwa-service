package sk.janobono.wiwa.business.model.application;

import lombok.Builder;

@Builder
public record ResetPasswordMailData(String subject, String title, String message, String passwordMessage, String link) {
}
