package sk.janobono.wiwa.business.impl.model.mail;

import lombok.Builder;

import java.io.File;
import java.util.List;
import java.util.Map;

@Builder
public record MailData(
        String from,
        String replyTo,
        List<String> recipients,
        List<String> cc,
        String subject,
        MailTemplate template,
        MailContentData content,
        Map<String, File> attachments
) {
}
