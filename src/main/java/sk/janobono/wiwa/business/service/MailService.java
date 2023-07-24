package sk.janobono.wiwa.business.service;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import sk.janobono.wiwa.business.model.mail.MailContentSo;
import sk.janobono.wiwa.business.model.mail.MailLinkSo;
import sk.janobono.wiwa.business.model.mail.MailSo;
import sk.janobono.wiwa.business.model.mail.MailTemplate;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    public void sendEmail(final MailSo mail) {
        final MimeMessagePreparator mimeMessagePreparator = mimeMessage -> {
            final MimeMessageHelper messageHelper = new MimeMessageHelper(
                    mimeMessage,
                    mail.attachments() != null && !mail.attachments().isEmpty()
            );
            messageHelper.setFrom(mail.from());
            if (!Optional.ofNullable(mail.replyTo()).map(String::isBlank).orElse(true)) {
                messageHelper.setReplyTo(mail.replyTo());
            }
            mail.recipients().forEach(recipient -> {
                try {
                    mimeMessage.addRecipients(Message.RecipientType.TO, recipient);
                } catch (final MessagingException e) {
                    e.printStackTrace();
                }
            });
            messageHelper.setSubject(mail.subject());
            messageHelper.setText(format(mail.template(), mail.content()), mail.template().getHtml());
            if (mail.attachments() != null) {
                mail.attachments().forEach(attachment -> {
                    try {
                        messageHelper.addAttachment(attachment.getName(), attachment);
                    } catch (final MessagingException e) {
                        e.printStackTrace();
                    }
                });
            }
        };
        javaMailSender.send(mimeMessagePreparator);
    }

    private String format(final MailTemplate template, final MailContentSo mailBaseContent) {
        return templateEngine.process(template.getTemplate(), getContext(mailBaseContent));
    }

    private IContext getContext(final MailContentSo mailBaseContent) {
        final Context context = new Context();
        context.setVariable("title", mailBaseContent.title());
        context.setVariable("lines", mailBaseContent.lines());
        context.setVariable("isLink", Optional.ofNullable(mailBaseContent.mailLink()).isPresent());
        context.setVariable("linkHref", Optional.ofNullable(mailBaseContent.mailLink()).map(MailLinkSo::href).orElse(""));
        context.setVariable("linkText", Optional.ofNullable(mailBaseContent.mailLink()).map(MailLinkSo::text).orElse(""));
        return context;
    }
}
