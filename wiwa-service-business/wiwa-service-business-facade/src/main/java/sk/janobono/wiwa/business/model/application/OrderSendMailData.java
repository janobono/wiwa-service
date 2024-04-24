package sk.janobono.wiwa.business.model.application;

public record OrderSendMailData(String subject, String title, String message, String link, String attachment) {
}
