package sk.janobono.wiwa.business.impl.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;
import sk.janobono.wiwa.business.impl.model.mail.MailContentData;
import sk.janobono.wiwa.business.impl.model.mail.MailData;
import sk.janobono.wiwa.business.impl.model.mail.MailLinkData;

import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class MailUtilServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    private MailUtilService mailUtilService;

    private AtomicReference<MimeMessagePreparator> sendResult;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        sendResult = new AtomicReference<>();
        Mockito.doAnswer(answer -> {
            sendResult.set(answer.getArgument(0));
            return null;
        }).when(javaMailSender).send(Mockito.any(MimeMessagePreparator.class));

        final SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setApplicationContext(new StaticApplicationContext());
        templateResolver.setPrefix("classpath:/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);

        final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setEnableSpringELCompiler(true);
        templateEngine.setTemplateResolver(templateResolver);

        mailUtilService = new MailUtilService(javaMailSender, templateEngine);
    }

    @Test
    void sendEmail_whenValidData_thenTheseResults() throws Exception {
        final Path src = Paths.get(Objects.requireNonNull(getClass().getResource("/linux.png")).toURI());
        final Path attachment = Path.of("./target/linux.png");
        Files.copy(src, attachment);

        assertThat(attachment.toFile().exists()).isTrue();
        mailUtilService.sendEmail(MailData.builder()
                .from("test@test.com")
                .replyTo("noreply@test.com")
                .recipients(List.of("recipient@domain.com"))
                .cc(List.of("cc@domain.com"))
                .subject("test")
                .content(MailContentData.builder()
                        .title("title")
                        .lines(List.of("line1", "line2", "line3"))
                        .mailLink(MailLinkData.builder()
                                .href("https://example.com")
                                .text("link text")
                                .build())
                        .build())
                .attachments(Map.of("image", attachment.toFile()))
                .build());
        assertThat(sendResult.get()).isNotNull();
        assertThat(attachment.toFile().exists()).isFalse();
    }
}
