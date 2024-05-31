package sk.janobono.wiwa.business.model.application;

import lombok.Builder;

@Builder
public record OrderSendMailData(String subject, String title, String message, String link, String attachment) {
}
