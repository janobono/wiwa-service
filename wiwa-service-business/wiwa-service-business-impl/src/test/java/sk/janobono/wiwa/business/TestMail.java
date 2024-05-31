package sk.janobono.wiwa.business;

import org.mockito.Mockito;
import sk.janobono.wiwa.business.impl.model.mail.MailData;
import sk.janobono.wiwa.business.impl.util.MailUtilService;

import java.util.concurrent.atomic.AtomicReference;

public class TestMail {

    private final AtomicReference<MailData> mail = new AtomicReference<>();

    public void mock(final MailUtilService mailUtilService) {
        mail.set(null);
        Mockito.doAnswer(answer -> {
                    final MailData data = answer.getArgument(0);
                    mail.set(data);
                    return null;
                })
                .when(mailUtilService).sendEmail(Mockito.any(MailData.class));
    }

    public MailData getMail() {
        return mail.get();
    }
}
