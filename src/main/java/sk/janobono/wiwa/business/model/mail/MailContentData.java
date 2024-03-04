package sk.janobono.wiwa.business.model.mail;

import java.util.List;

public record MailContentData(String title, List<String> lines, MailLinkData mailLink) {
}
