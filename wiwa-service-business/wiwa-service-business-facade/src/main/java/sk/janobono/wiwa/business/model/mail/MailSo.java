package sk.janobono.wiwa.business.model.mail;

import java.io.File;
import java.util.List;

public record MailSo(
        String from,
        String replyTo,
        List<String> recipients,
        String subject,
        MailTemplate template,
        MailContentSo content,
        List<File> attachments
) {
}
