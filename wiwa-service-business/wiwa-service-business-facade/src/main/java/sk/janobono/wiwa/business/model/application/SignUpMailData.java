package sk.janobono.wiwa.business.model.application;

import lombok.Builder;

@Builder
public record SignUpMailData(String subject, String title, String message, String link) {
}
