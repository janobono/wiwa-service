package sk.janobono.wiwa.business.impl.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.support.StaticApplicationContext;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;
import sk.janobono.wiwa.business.model.application.PDFPropertiesData;
import sk.janobono.wiwa.business.model.order.OrderData;
import sk.janobono.wiwa.business.service.ApplicationPropertyService;

import java.nio.file.Path;

class OrderPdfUtilServiceTest {

    @Mock
    private ApplicationPropertyService applicationPropertyService;

    private OrderPdfUtilService orderPdfUtilService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Mockito.when(applicationPropertyService.getPDFProperties()).thenReturn(
                new PDFPropertiesData(
                        "Order No.%03d"
                )
        );

        final SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setApplicationContext(new StaticApplicationContext());
        templateResolver.setPrefix("classpath:/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);

        final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setEnableSpringELCompiler(true);
        templateEngine.setTemplateResolver(templateResolver);

        orderPdfUtilService = new OrderPdfUtilService(templateEngine, applicationPropertyService);
    }

    @Test
    void generatePdf_whenOrder_thenTheseResults() {
        final OrderData order = OrderData.builder()
                .id(1L)
                .orderNumber(1L)
                // TODO
                .build();
        Path data = null;
        try {
            data = orderPdfUtilService.generatePdf(order);

        } finally {
            if (data != null) {
                data.toFile().delete();
            }
        }
    }

}
