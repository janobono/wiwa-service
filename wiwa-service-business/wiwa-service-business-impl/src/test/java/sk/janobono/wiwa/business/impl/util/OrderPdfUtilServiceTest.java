package sk.janobono.wiwa.business.impl.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.support.StaticApplicationContext;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;
import sk.janobono.wiwa.business.impl.model.pdf.PdfContentData;
import sk.janobono.wiwa.business.model.application.PDFPropertiesData;
import sk.janobono.wiwa.business.model.application.UnitData;
import sk.janobono.wiwa.business.model.order.OrderContactData;
import sk.janobono.wiwa.business.model.order.OrderData;
import sk.janobono.wiwa.business.model.order.OrderUserData;
import sk.janobono.wiwa.business.model.order.summary.OrderGlueSummaryData;
import sk.janobono.wiwa.business.model.order.summary.OrderSummaryData;
import sk.janobono.wiwa.business.service.ApplicationPropertyService;
import sk.janobono.wiwa.config.CommonConfigProperties;
import sk.janobono.wiwa.model.*;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class OrderPdfUtilServiceTest {

    @Mock
    private ApplicationPropertyService applicationPropertyService;

    @Mock
    private MaterialUtilService materialUtilService;

    private OrderPdfUtilService orderPdfUtilService;

    private OrderData order;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Mockito.when(applicationPropertyService.getPDFProperties()).thenReturn(
                new PDFPropertiesData(
                        Map.of(
                                PDFFormat.TITLE, "Order No.%s",
                                PDFFormat.ORDER_NUMBER, "%03d",
                                PDFFormat.INTEGER, "%d %s",
                                PDFFormat.UNIT, "%.3f %s",
                                PDFFormat.PRICE, "%.2f %s",
                                PDFFormat.EDGE_FORMAT, "%s %dx%.1f"
                        ),
                        Map.of(
                                PdfContent.MATERIAL_NOT_FOUND, "Material not found",
                                PdfContent.BOARD_NOT_FOUND, "Board not found",
                                PdfContent.EDGE_NOT_FOUND, "Edge not found"
                        ),
                        Map.of(
                                OrderPackageType.NO_PACKAGE, "NO_PACKAGE",
                                OrderPackageType.NO_PACKAGE_WITH_REMAINS, "NO_PACKAGE_WITH_REMAINS",
                                OrderPackageType.PACKAGE, "PACKAGE",
                                OrderPackageType.PACKAGE_WITH_REMAINS, "PACKAGE_WITH_REMAINS"
                        )
                )
        );

        Mockito.when(applicationPropertyService.getUnits()).thenReturn(
                List.of(
                        new UnitData(Unit.MILLIMETER, "mm"),
                        new UnitData(Unit.METER, "m"),
                        new UnitData(Unit.SQUARE_METER, "㎡"),
                        new UnitData(Unit.KILOGRAM, "kg"),
                        new UnitData(Unit.PIECE, "p")
                )
        );

        Mockito.when(materialUtilService.getMaterialNames(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(
                Map.of(1L, "material1")
        );

        final SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setApplicationContext(new StaticApplicationContext());
        templateResolver.setPrefix("classpath:/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);

        final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setEnableSpringELCompiler(true);
        templateEngine.setTemplateResolver(templateResolver);

        orderPdfUtilService = new OrderPdfUtilService(
                new CommonConfigProperties(
                        "en_EN",
                        "Wiwa",
                        "Woodworking Industry Web Application",
                        "http://localhost:5173",
                        "/ui/confirm/",
                        "/fixme/",
                        "mail@wiwa.sk",
                        "orders@wiwa.sk",
                        1000,
                        130,
                        4,
                        new Currency("EUR", "€")
                ),
                templateEngine,
                applicationPropertyService,
                materialUtilService
        );

        order = OrderData.builder()
                .id(1L)
                .creator(OrderUserData.builder()
                        .titleBefore("mr.")
                        .firstName("Jimbo")
                        .lastName("Tester")
                        .build())
                .created(LocalDateTime.of(2024, 1, 1, 12, 0))
                .orderNumber(1L)
                .deliveryDate(LocalDate.of(2024, 1, 1))
                .packageType(OrderPackageType.PACKAGE_WITH_REMAINS)

                .contact(OrderContactData.builder()
                        .name("Mr. Wood Pecker")
                        .street("Oak alley 12")
                        .zipCode("000 00")
                        .city("Wooden")
                        .state("Forestland")
                        .phone("0905 505 505")
                        .email("wood.pecker@sekvoi.tree")
                        .businessId("B001001")
                        .taxId("T001001")
                        .build())

                .summary(OrderSummaryData.builder()
                        .boardSummary(List.of())
                        .edgeSummary(List.of())
                        .glueSummary(OrderGlueSummaryData.builder()
                                .area(BigDecimal.valueOf(4.35))
                                .price(BigDecimal.valueOf(100.000))
                                .vatPrice(BigDecimal.valueOf(120.000))
                                .build())
                        .cutSummary(List.of())
                        .weight(BigDecimal.valueOf(123.123))
                        .total(BigDecimal.valueOf(100.000))
                        .vatTotal(BigDecimal.valueOf(120.000))
                        .build())
                // TODO
                .build();
    }

    @Test
    void orderToPdfContent_whenOrder_thenTheseResults() {
        final PdfContentData pdfContentData = orderPdfUtilService.orderToPdfContent(applicationPropertyService.getPDFProperties(), order);

        assertThat(pdfContentData.title()).isEqualTo("Order No.001");
        assertThat(pdfContentData.creator()).isEqualTo("mr. Jimbo Tester");
        assertThat(pdfContentData.created()).isEqualTo("01.01.2024 12:00");
        assertThat(pdfContentData.orderNumber()).isEqualTo("001");
        assertThat(pdfContentData.deliveryDate()).isEqualTo("01.01.2024");
        assertThat(pdfContentData.packageType()).isEqualTo("PACKAGE_WITH_REMAINS");

        assertThat(pdfContentData.orderContact()).isNotNull();
        assertThat(pdfContentData.orderContact().name()).isEqualTo("Mr. Wood Pecker");
        assertThat(pdfContentData.orderContact().street()).isEqualTo("Oak alley 12");
        assertThat(pdfContentData.orderContact().zipCode()).isEqualTo("000 00");
        assertThat(pdfContentData.orderContact().city()).isEqualTo("Wooden");
        assertThat(pdfContentData.orderContact().state()).isEqualTo("Forestland");
        assertThat(pdfContentData.orderContact().phone()).isEqualTo("0905 505 505");
        assertThat(pdfContentData.orderContact().email()).isEqualTo("wood.pecker@sekvoi.tree");
        assertThat(pdfContentData.orderContact().businessId()).isEqualTo("B001001");
        assertThat(pdfContentData.orderContact().taxId()).isEqualTo("T001001");

        assertThat(pdfContentData.summary()).isNotNull();

        assertThat(pdfContentData.summary().weight()).isEqualTo("123.123 kg");
        assertThat(pdfContentData.summary().total()).isEqualTo("100.00 €");
        assertThat(pdfContentData.summary().vatTotal()).isEqualTo("120.00 €");
    }

    @Disabled
    @Test
    void generatePdf_whenOrder_thenTheseResults() {
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
