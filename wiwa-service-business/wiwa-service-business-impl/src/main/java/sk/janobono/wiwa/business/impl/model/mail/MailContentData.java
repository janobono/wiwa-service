package sk.janobono.wiwa.business.impl.model.mail;

import lombok.Builder;

import java.util.List;

@Builder
public record MailContentData(String title, List<String> lines, MailLinkData mailLink) {
}
