package sk.janobono.wiwa.business.model.application;

import lombok.Builder;

@Builder
public record OrderCommentMailData(String subject, String title, String message, String link) {
}
