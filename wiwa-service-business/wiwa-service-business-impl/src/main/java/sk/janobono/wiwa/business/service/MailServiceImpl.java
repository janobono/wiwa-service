package sk.janobono.wiwa.business.service;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import sk.janobono.wiwa.business.model.mail.MailContentSo;
import sk.janobono.wiwa.business.model.mail.MailSo;
import sk.janobono.wiwa.business.model.mail.MailTemplate;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class MailServiceImpl implements MailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    public void sendEmail(MailSo mail) {
        log.debug("sendMail({})", mail);
        MimeMessagePreparator mimeMessagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(
                    mimeMessage,
                    mail.attachments() != null && !mail.attachments().isEmpty()
            );
            messageHelper.setFrom(mail.from());
            if (StringUtils.hasLength(mail.replyTo())) {
                messageHelper.setReplyTo(mail.replyTo());
            }
            mail.recipients().forEach(recipient -> {
                try {
                    mimeMessage.addRecipients(Message.RecipientType.TO, recipient);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            });
            messageHelper.setSubject(mail.subject());
            messageHelper.setText(format(mail.template(), mail.content()), mail.template().getHtml());
            if (mail.attachments() != null) {
                mail.attachments().forEach(attachment -> {
                    try {
                        messageHelper.addAttachment(attachment.getName(), attachment);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                });
            }
        };
        javaMailSender.send(mimeMessagePreparator);
    }

    private String format(MailTemplate template, MailContentSo mailBaseContentSo) {
        log.debug("format({},{})", template, mailBaseContentSo);
        return templateEngine.process(template.getTemplate(), getContext(mailBaseContentSo));
    }

    private IContext getContext(MailContentSo mailBaseContentSo) {
        Context context = new Context();
        context.setVariable("title", mailBaseContentSo.title());
        context.setVariable("lines", mailBaseContentSo.lines());
        context.setVariable("isLink", Objects.nonNull(mailBaseContentSo.mailLink()));
        context.setVariable("linkHref", Objects.nonNull(mailBaseContentSo.mailLink()) ? mailBaseContentSo.mailLink().href() : "");
        context.setVariable("linkText", Objects.nonNull(mailBaseContentSo.mailLink()) ? mailBaseContentSo.mailLink().text() : "");
        return context;
    }
}
