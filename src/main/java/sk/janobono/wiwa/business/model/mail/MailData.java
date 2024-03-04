package sk.janobono.wiwa.business.model.mail;

import java.io.File;
import java.util.List;

public record MailData(
        String from,
        String replyTo,
        List<String> recipients,
        String subject,
        MailTemplate template,
        MailContentData content,
        List<File> attachments
) {
}
