package sk.janobono.wiwa.business.impl.model.mail;

import lombok.Builder;

@Builder
public record MailLinkData(String href, String text) {
}
