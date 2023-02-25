package sk.janobono.wiwa.business.service;

import sk.janobono.wiwa.business.model.mail.MailSo;

public interface MailService {
    void sendEmail(MailSo mail);
}
