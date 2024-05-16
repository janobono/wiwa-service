package sk.janobono.wiwa.business.impl.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.support.StaticApplicationContext;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;
import sk.janobono.wiwa.business.impl.model.pdf.PdfContentData;
import sk.janobono.wiwa.business.model.application.OrderPropertiesData;
import sk.janobono.wiwa.business.model.application.UnitData;
import sk.janobono.wiwa.business.model.order.*;
import sk.janobono.wiwa.business.model.order.summary.*;
import sk.janobono.wiwa.business.service.ApplicationPropertyService;
import sk.janobono.wiwa.config.CommonConfigProperties;
import sk.janobono.wiwa.model.*;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
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

        final ObjectMapper objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();

        Mockito.when(applicationPropertyService.getOrderProperties()).thenReturn(
                new OrderPropertiesData(
                        new HashMap<>(),
                        new HashMap<>(),
                        new HashMap<>(),
                        new HashMap<>() {{
                            put(OrderFormat.PDF_TITLE, "Order No.%s");
                            put(OrderFormat.PDF_ORDER_NUMBER, "%03d");
                            put(OrderFormat.PDF_INTEGER, "%d %s");
                            put(OrderFormat.PDF_UNIT, "%.3f %s");
                            put(OrderFormat.PDF_PRICE, "%.2f %s");
                            put(OrderFormat.PDF_EDGE, "%s %dx%.1f");
                        }},
                        new HashMap<>() {{
                            put(OrderContent.MATERIAL_NOT_FOUND, "Material not found");
                            put(OrderContent.BOARD_NOT_FOUND, "Board not found");
                            put(OrderContent.EDGE_NOT_FOUND, "Edge not found");
                            put(OrderContent.CREATOR, "creator:");
                            put(OrderContent.CREATED, "created:");
                            put(OrderContent.ORDER_NUMBER, "order number:");
                            put(OrderContent.DELIVERY_DATE, "delivery date:");
                            put(OrderContent.PACKAGE_TYPE, "package type:");
                            put(OrderContent.CONTACT_INFO, "Contact info");
                            put(OrderContent.NAME, "name");
                            put(OrderContent.STREET, "street");
                            put(OrderContent.ZIP_CODE, "zip code");
                            put(OrderContent.CITY, "city");
                            put(OrderContent.STATE, "state");
                            put(OrderContent.PHONE, "phone");
                            put(OrderContent.EMAIL, "email");
                            put(OrderContent.BUSINESS_ID, "business id");
                            put(OrderContent.TAX_ID, "tax id");
                            put(OrderContent.ORDER_SUMMARY, "Order summary");
                            put(OrderContent.BOARD_SUMMARY, "Board consumption");
                            put(OrderContent.BOARD_SUMMARY_AREA, "area");
                            put(OrderContent.BOARD_SUMMARY_COUNT, "board count");
                            put(OrderContent.BOARD_SUMMARY_WEIGHT, "weight");
                            put(OrderContent.BOARD_SUMMARY_PRICE, "price");
                            put(OrderContent.BOARD_SUMMARY_VAT_PRICE, "vat price");
                            put(OrderContent.EDGE_SUMMARY, "Edge consumption");
                            put(OrderContent.EDGE_SUMMARY_LENGTH, "length");
                            put(OrderContent.EDGE_SUMMARY_GLUE_LENGTH, "glue length");
                            put(OrderContent.EDGE_SUMMARY_EDGE_PRICE, "edge price");
                            put(OrderContent.EDGE_SUMMARY_EDGE_VAT_PRICE, "edge vat price");
                            put(OrderContent.EDGE_SUMMARY_GLUE_PRICE, "glue price");
                            put(OrderContent.EDGE_SUMMARY_GLUE_VAT_PRICE, "glue vat price");
                            put(OrderContent.GLUE_SUMMARY, "Glue area");
                            put(OrderContent.GLUE_SUMMARY_AREA, "area");
                            put(OrderContent.GLUE_SUMMARY_PRICE, "price");
                            put(OrderContent.GLUE_SUMMARY_VAT_PRICE, "vat price");
                            put(OrderContent.CUT_SUMMARY, "Formatting");
                            put(OrderContent.CUT_SUMMARY_THICKNESS, "thickness");
                            put(OrderContent.CUT_SUMMARY_AMOUNT, "amount");
                            put(OrderContent.CUT_SUMMARY_PRICE, "price");
                            put(OrderContent.CUT_SUMMARY_VAT_PRICE, "vat price");
                            put(OrderContent.TOTAL_SUMMARY, "Total");
                            put(OrderContent.TOTAL_SUMMARY_WEIGHT, "weight");
                            put(OrderContent.TOTAL_SUMMARY_PRICE, "price");
                            put(OrderContent.TOTAL_SUMMARY_VAT_PRICE, "vat price");
                            put(OrderContent.PARTS_LIST, "Part list");
                        }},
                        new HashMap<>(),
                        ";",
                        Map.of("<.*?>", "", "\\s+", "_"),
                        new HashMap<>(),
                        800,
                        18
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
        Mockito.when(materialUtilService.findBoard(Mockito.any(), Mockito.anyLong())).thenCallRealMethod();
        Mockito.when(materialUtilService.findEdge(Mockito.any(), Mockito.anyLong())).thenCallRealMethod();
        Mockito.when(materialUtilService.getDecor(Mockito.any(), Mockito.anyLong(), Mockito.anyString())).thenCallRealMethod();
        Mockito.when(materialUtilService.getEdge(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenCallRealMethod();

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

                .boards(List.of(
                        OrderBoardData.builder()
                                .id(1L)
                                .code("code")
                                .name("name")
                                .boardCode("b-code")
                                .structureCode("s-code")
                                .orientation(false)
                                .weight(BigDecimal.valueOf(1))
                                .length(BigDecimal.valueOf(2000))
                                .width(BigDecimal.valueOf(1500))
                                .thickness(BigDecimal.valueOf(10))
                                .price(BigDecimal.valueOf(100))
                                .build()
                ))

                .edges(List.of(
                        OrderEdgeData.builder()
                                .id(1L)
                                .code("code")
                                .name("name")
                                .weight(BigDecimal.valueOf(0.23))
                                .width(BigDecimal.valueOf(19))
                                .thickness(BigDecimal.valueOf(0.8))
                                .price(BigDecimal.valueOf(100))
                                .build()
                ))

                .summary(OrderSummaryData.builder()
                        .boardSummary(List.of(
                                OrderBoardSummaryData.builder()
                                        .id(1L)
                                        .area(BigDecimal.valueOf(4.3))
                                        .boardsCount(BigDecimal.TWO)
                                        .weight(BigDecimal.valueOf(10.24))
                                        .price(BigDecimal.valueOf(100.000))
                                        .vatPrice(BigDecimal.valueOf(120.000))
                                        .build()
                        ))
                        .edgeSummary(List.of(
                                OrderEdgeSummaryData.builder()
                                        .id(1L)
                                        .length(BigDecimal.valueOf(4.2))
                                        .glueLength(BigDecimal.valueOf(3.8))
                                        .weight(BigDecimal.valueOf(0.83))
                                        .edgePrice(BigDecimal.valueOf(12.12))
                                        .edgeVatPrice(BigDecimal.valueOf(15.15))
                                        .gluePrice(BigDecimal.valueOf(7.54))
                                        .glueVatPrice(BigDecimal.valueOf(8.56))
                                        .build()
                        ))
                        .glueSummary(OrderGlueSummaryData.builder()
                                .area(BigDecimal.valueOf(4.35))
                                .price(BigDecimal.valueOf(100.000))
                                .vatPrice(BigDecimal.valueOf(120.000))
                                .build())
                        .cutSummary(List.of(
                                OrderCutSummaryData.builder()
                                        .thickness(BigDecimal.valueOf(10.0))
                                        .amount(BigDecimal.valueOf(6.7))
                                        .price(BigDecimal.valueOf(2.68))
                                        .vatPrice(BigDecimal.valueOf(3.03))
                                        .build()
                        ))
                        .weight(BigDecimal.valueOf(123.123))
                        .total(BigDecimal.valueOf(100.000))
                        .vatTotal(BigDecimal.valueOf(120.000))
                        .build())


                // TODO
                .build();
    }

    @Test
    void orderToPdfContent_whenOrder_thenTheseResults() {
        final PdfContentData pdfContentData = orderPdfUtilService.orderToPdfContent(applicationPropertyService.getOrderProperties(), order);

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

        assertThat(pdfContentData.summary().boardSummary().size()).isEqualTo(1);
        assertThat(pdfContentData.summary().boardSummary().get(0).material()).isEqualTo("material1");
        assertThat(pdfContentData.summary().boardSummary().get(0).name()).isEqualTo("b-code s-code name");
        assertThat(pdfContentData.summary().boardSummary().get(0).area()).isEqualTo("4.300 ㎡");
        assertThat(pdfContentData.summary().boardSummary().get(0).boardsCount()).isEqualTo("2 p");
        assertThat(pdfContentData.summary().boardSummary().get(0).weight()).isEqualTo("10.240 kg");
        assertThat(pdfContentData.summary().boardSummary().get(0).price()).isEqualTo("100.00 €");
        assertThat(pdfContentData.summary().boardSummary().get(0).vatPrice()).isEqualTo("120.00 €");

        assertThat(pdfContentData.summary().edgeSummary().size()).isEqualTo(1);
        assertThat(pdfContentData.summary().edgeSummary().get(0).name()).isEqualTo("code 19x0.8");
        assertThat(pdfContentData.summary().edgeSummary().get(0).length()).isEqualTo("4.200 m");
        assertThat(pdfContentData.summary().edgeSummary().get(0).glueLength()).isEqualTo("3.800 m");
        assertThat(pdfContentData.summary().edgeSummary().get(0).weight()).isEqualTo("0.830 kg");
        assertThat(pdfContentData.summary().edgeSummary().get(0).edgePrice()).isEqualTo("12.12 €");
        assertThat(pdfContentData.summary().edgeSummary().get(0).edgeVatPrice()).isEqualTo("15.15 €");
        assertThat(pdfContentData.summary().edgeSummary().get(0).gluePrice()).isEqualTo("7.54 €");
        assertThat(pdfContentData.summary().edgeSummary().get(0).glueVatPrice()).isEqualTo("8.56 €");

        assertThat(pdfContentData.summary().glueSummary().area()).isEqualTo("4.350 ㎡");
        assertThat(pdfContentData.summary().glueSummary().price()).isEqualTo("100.00 €");
        assertThat(pdfContentData.summary().glueSummary().vatPrice()).isEqualTo("120.00 €");

        assertThat(pdfContentData.summary().cutSummary().size()).isEqualTo(1);
        assertThat(pdfContentData.summary().cutSummary().get(0).thickness()).isEqualTo("10 mm");
        assertThat(pdfContentData.summary().cutSummary().get(0).amount()).isEqualTo("6.700 m");
        assertThat(pdfContentData.summary().cutSummary().get(0).price()).isEqualTo("2.68 €");
        assertThat(pdfContentData.summary().cutSummary().get(0).vatPrice()).isEqualTo("3.03 €");

        assertThat(pdfContentData.summary().weight()).isEqualTo("123.123 kg");
        assertThat(pdfContentData.summary().total()).isEqualTo("100.00 €");
        assertThat(pdfContentData.summary().vatTotal()).isEqualTo("120.00 €");
    }

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
