package sk.janobono.wiwa.business.impl.util;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import sk.janobono.wiwa.business.model.mail.MailContentData;
import sk.janobono.wiwa.business.model.mail.MailData;
import sk.janobono.wiwa.business.model.mail.MailLinkData;
import sk.janobono.wiwa.business.model.mail.MailTemplate;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MailUtilService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Async
    public void sendEmail(final MailData mail) {
        final MimeMessagePreparator mimeMessagePreparator = mimeMessage -> {
            final MimeMessageHelper messageHelper = new MimeMessageHelper(
                    mimeMessage,
                    mail.attachments() != null && !mail.attachments().isEmpty()
            );
            messageHelper.setFrom(mail.from());
            if (Optional.ofNullable(mail.replyTo()).filter(s -> !s.isBlank()).isPresent()) {
                messageHelper.setReplyTo(mail.replyTo());
            }
            mail.recipients().forEach(recipient -> {
                try {
                    mimeMessage.addRecipients(Message.RecipientType.TO, recipient);
                } catch (final MessagingException e) {
                    throw new RuntimeException(e);
                }
            });
            messageHelper.setSubject(mail.subject());
            messageHelper.setText(format(mail.template(), mail.content()), mail.template().getHtml());
            if (mail.attachments() != null) {
                mail.attachments().forEach(attachment -> {
                    try {
                        messageHelper.addAttachment(attachment.getName(), attachment);
                    } catch (final MessagingException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        };
        javaMailSender.send(mimeMessagePreparator);
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
