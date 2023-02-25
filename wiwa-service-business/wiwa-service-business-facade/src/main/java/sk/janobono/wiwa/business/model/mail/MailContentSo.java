package sk.janobono.wiwa.business.model.mail;

import java.util.List;

public record MailContentSo(String title, List<String> lines, MailLinkSo mailLink) {
}
