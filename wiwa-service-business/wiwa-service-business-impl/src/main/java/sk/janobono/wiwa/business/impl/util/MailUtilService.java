package sk.janobono.wiwa.business.impl.util;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import sk.janobono.wiwa.business.impl.model.mail.MailContentData;
import sk.janobono.wiwa.business.impl.model.mail.MailData;
import sk.janobono.wiwa.business.impl.model.mail.MailLinkData;
import sk.janobono.wiwa.business.impl.model.mail.MailTemplate;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class MailUtilService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Async
    public void sendEmail(final MailData mail) {
        try {
            final MimeMessagePreparator mimeMessagePreparator = mimeMessage -> {
                final MimeMessageHelper messageHelper = new MimeMessageHelper(
                        mimeMessage,
                        Optional.ofNullable(mail.attachments()).map(att -> !att.isEmpty()).orElse(false)
                );

                messageHelper.setFrom(mail.from());

                if (Optional.ofNullable(mail.replyTo()).filter(s -> !s.isBlank()).isPresent()) {
                    messageHelper.setReplyTo(mail.replyTo());
                }

                Optional.ofNullable(mail.recipients()).stream().flatMap(Collection::stream).forEach(recipient -> {
                    try {
                        mimeMessage.addRecipients(Message.RecipientType.TO, recipient);
                    } catch (final MessagingException e) {
                        throw new RuntimeException(e);
                    }
                });

                Optional.ofNullable(mail.cc()).stream().flatMap(Collection::stream).forEach(cc -> {
                    try {
                        mimeMessage.addRecipients(Message.RecipientType.CC, cc);
                    } catch (final MessagingException e) {
                        throw new RuntimeException(e);
                    }
                });

                messageHelper.setSubject(mail.subject());
                messageHelper.setText(format(mail.template(), mail.content()), mail.template().getHtml());

                Optional.ofNullable(mail.attachments()).map(Map::entrySet).stream().flatMap(Collection::stream).forEach(attachment -> {
                    try {
                        messageHelper.addAttachment(attachment.getKey(), attachment.getValue());
                    } catch (final MessagingException e) {
                        throw new RuntimeException(e);
                    }
                });
            };
            javaMailSender.send(mimeMessagePreparator);
        } finally {
            Optional.ofNullable(mail.attachments()).map(Map::values).stream().flatMap(Collection::stream)
                    .forEach(attachment -> {
                        final boolean deleted = attachment.delete();
                        if (!deleted) {
                            log.warn("Attachment not deleted {}", attachment);
                        }
                    });
        }
    }

    private String format(final MailTemplate template, final MailContentData mailBaseContent) {
        return templateEngine.process(template.getTemplate(), getContext(mailBaseContent));
    }

    private IContext getContext(final MailContentData mailBaseContent) {
        final Context context = new Context();
        context.setVariable("title", mailBaseContent.title());
        context.setVariable("lines", mailBaseContent.lines());
        context.setVariable("isLink", Optional.ofNullable(mailBaseContent.mailLink()).isPresent());
        context.setVariable("linkHref", Optional.ofNullable(mailBaseContent.mailLink()).map(MailLinkData::href).orElse(""));
        context.setVariable("linkText", Optional.ofNullable(mailBaseContent.mailLink()).map(MailLinkData::text).orElse(""));
        return context;
    }
}
