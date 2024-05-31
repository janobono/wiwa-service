package sk.janobono.wiwa.business.impl.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.support.StaticApplicationContext;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;
import sk.janobono.wiwa.business.TestConfigProperties;
import sk.janobono.wiwa.business.TestRepositories;
import sk.janobono.wiwa.business.impl.ApplicationPropertyServiceImpl;
import sk.janobono.wiwa.business.impl.model.html.HtmlContentData;
import sk.janobono.wiwa.business.model.DimensionsData;
import sk.janobono.wiwa.business.model.order.*;
import sk.janobono.wiwa.business.model.order.part.*;
import sk.janobono.wiwa.business.model.order.summary.*;
import sk.janobono.wiwa.business.service.ApplicationPropertyService;
import sk.janobono.wiwa.component.ImageUtil;
import sk.janobono.wiwa.config.CommonConfigProperties;
import sk.janobono.wiwa.config.JwtConfigProperties;
import sk.janobono.wiwa.dal.domain.CodeListDo;
import sk.janobono.wiwa.dal.domain.CodeListItemDo;
import sk.janobono.wiwa.dal.repository.ApplicationPropertyRepository;
import sk.janobono.wiwa.dal.repository.BoardCodeListItemRepository;
import sk.janobono.wiwa.dal.repository.CodeListItemRepository;
import sk.janobono.wiwa.dal.repository.CodeListRepository;
import sk.janobono.wiwa.model.Currency;
import sk.janobono.wiwa.model.FrameType;
import sk.janobono.wiwa.model.OrderPackageType;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderHtmlUtilServiceTest {

    private ApplicationPropertyService applicationPropertyService;
    private OrderHtmlUtilService orderHtmlUtilService;
    private OrderData order;

    @BeforeEach
    void setUp() {
        final CommonConfigProperties commonConfigProperties = Mockito.mock(CommonConfigProperties.class);
        final JwtConfigProperties jwtConfigProperties = Mockito.mock(JwtConfigProperties.class);
        final TestConfigProperties testConfigProperties = new TestConfigProperties();
        testConfigProperties.mock(commonConfigProperties);
        testConfigProperties.mock(jwtConfigProperties);

        final ObjectMapper objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();

        final ApplicationPropertyRepository applicationPropertyRepository = Mockito.mock(ApplicationPropertyRepository.class);
        final CodeListItemRepository codeListItemRepository = Mockito.mock(CodeListItemRepository.class);
        final CodeListRepository codeListRepository = Mockito.mock(CodeListRepository.class);
        final BoardCodeListItemRepository boardCodeListItemRepository = Mockito.mock(BoardCodeListItemRepository.class);
        final TestRepositories testRepositories = new TestRepositories();
        testRepositories.mock(applicationPropertyRepository);
        testRepositories.mock(codeListItemRepository);
        testRepositories.mock(codeListRepository);
        testRepositories.mock(boardCodeListItemRepository);

        applicationPropertyService = new ApplicationPropertyServiceImpl(
                objectMapper, commonConfigProperties, jwtConfigProperties, new PropertyUtilService(applicationPropertyRepository), codeListRepository
        );

        final CodeListDo codeList = codeListRepository.save(CodeListDo.builder().code("code").name("materials").build());
        final CodeListItemDo codeListItem = codeListItemRepository.save(CodeListItemDo.builder()
                .codeListId(codeList.getId())
                .value("material1")
                .build());
        boardCodeListItemRepository.saveAll(1L, List.of(codeListItem.getId()));
        applicationPropertyService.setBoardMaterialCategory(codeList.getId());

        final SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setApplicationContext(new StaticApplicationContext());
        templateResolver.setPrefix("classpath:/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);

        final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setEnableSpringELCompiler(true);
        templateEngine.setTemplateResolver(templateResolver);

        orderHtmlUtilService = new OrderHtmlUtilService(
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
                new ImageUtil(),
                applicationPropertyService,
                new MaterialUtilService(boardCodeListItemRepository)
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

                .items(List.of(OrderItemData.builder()
                                .id(1L)
                                .sortNum(0)
                                .name("test0")
                                .description("test part 0")
                                .quantity(1)
                                .part(PartBasicData.builder()
                                        .orientation(true)
                                        .boardId(1L)
                                        .edgeIdA1(1L)
                                        .edgeIdA2(1L)
                                        .edgeIdB1(1L)
                                        .edgeIdB2(1L)
                                        .dimensionsTOP(new DimensionsData(BigDecimal.valueOf(250), BigDecimal.valueOf(250)))
                                        .cornerA1B1(new PartCornerStraightData(1L, new DimensionsData(BigDecimal.valueOf(80), BigDecimal.valueOf(100))))
                                        .cornerA1B2(new PartCornerStraightData(1L, new DimensionsData(BigDecimal.valueOf(75), BigDecimal.valueOf(75))))
                                        .cornerA2B1(new PartCornerRoundedData(1L, BigDecimal.valueOf(100)))
                                        .cornerA2B2(new PartCornerRoundedData(1L, BigDecimal.valueOf(90)))
                                        .build())
                                .summary(OrderItemSummaryData.builder()
                                        .partSummary(OrderItemPartSummaryData.builder()
                                                .boardSummary(List.of())
                                                .edgeSummary(List.of())
                                                .gluedArea(BigDecimal.ZERO)
                                                .cutSummary(List.of())
                                                .build())
                                        .totalSummary(OrderItemPartSummaryData.builder()
                                                .boardSummary(List.of())
                                                .edgeSummary(List.of())
                                                .gluedArea(BigDecimal.ZERO)
                                                .cutSummary(List.of())
                                                .build())
                                        .build()
                                ).build(),
                        OrderItemData.builder()
                                .id(2L)
                                .sortNum(1)
                                .name("test1")
                                .description("test part 1")
                                .quantity(1)
                                .part(PartDuplicatedBasicData.builder()
                                        .orientation(true)
                                        .boardId(1L)
                                        .boardIdBottom(1L)
                                        .edgeIdA1(1L)
                                        .edgeIdA2(1L)
                                        .edgeIdB1(1L)
                                        .edgeIdB2(1L)
                                        .dimensionsTOP(new DimensionsData(BigDecimal.valueOf(300), BigDecimal.valueOf(300)))
                                        .cornerA1B1(new PartCornerStraightData(1L, new DimensionsData(BigDecimal.valueOf(85), BigDecimal.valueOf(100))))
                                        .cornerA1B2(new PartCornerStraightData(1L, new DimensionsData(BigDecimal.valueOf(75), BigDecimal.valueOf(80))))
                                        .cornerA2B1(new PartCornerRoundedData(1L, BigDecimal.valueOf(100)))
                                        .cornerA2B2(new PartCornerRoundedData(1L, BigDecimal.valueOf(90)))
                                        .build())
                                .summary(OrderItemSummaryData.builder()
                                        .partSummary(OrderItemPartSummaryData.builder()
                                                .boardSummary(List.of())
                                                .edgeSummary(List.of())
                                                .gluedArea(BigDecimal.ZERO)
                                                .cutSummary(List.of())
                                                .build())
                                        .totalSummary(OrderItemPartSummaryData.builder()
                                                .boardSummary(List.of())
                                                .edgeSummary(List.of())
                                                .gluedArea(BigDecimal.ZERO)
                                                .cutSummary(List.of())
                                                .build())
                                        .build()
                                ).build(),
                        OrderItemData.builder()
                                .id(3L)
                                .sortNum(2)
                                .name("test2")
                                .description("test part 2")
                                .quantity(1)
                                .part(PartFrameData.builder()
                                        .frameType(FrameType.HORIZONTAL)
                                        .boardIdA1(1L)
                                        .boardIdA2(1L)
                                        .boardIdB1(1L)
                                        .boardIdB2(1L)
                                        .edgeIdA1(1L)
                                        .edgeIdA1I(1L)
                                        .edgeIdA2(1L)
                                        .edgeIdA2I(1L)
                                        .edgeIdB1(1L)
                                        .edgeIdB1I(1L)
                                        .edgeIdB2(1L)
                                        .edgeIdB2I(1L)
                                        .dimensionsTOP(new DimensionsData(BigDecimal.valueOf(600), BigDecimal.valueOf(600)))
                                        .dimensionsA1(new DimensionsData(BigDecimal.valueOf(600), BigDecimal.valueOf(100)))
                                        .dimensionsA2(new DimensionsData(BigDecimal.valueOf(600), BigDecimal.valueOf(100)))
                                        .dimensionsB1(new DimensionsData(BigDecimal.valueOf(100), BigDecimal.valueOf(400)))
                                        .dimensionsB2(new DimensionsData(BigDecimal.valueOf(100), BigDecimal.valueOf(400)))
                                        .build())
                                .summary(OrderItemSummaryData.builder()
                                        .partSummary(OrderItemPartSummaryData.builder()
                                                .boardSummary(List.of())
                                                .edgeSummary(List.of())
                                                .gluedArea(BigDecimal.ZERO)
                                                .cutSummary(List.of())
                                                .build())
                                        .totalSummary(OrderItemPartSummaryData.builder()
                                                .boardSummary(List.of())
                                                .edgeSummary(List.of())
                                                .gluedArea(BigDecimal.ZERO)
                                                .cutSummary(List.of())
                                                .build())
                                        .build()
                                ).build(),
                        OrderItemData.builder()
                                .id(4L)
                                .sortNum(3)
                                .name("test3")
                                .description("test part 3")
                                .quantity(1)
                                .part(PartDuplicatedFrameData.builder()
                                        .frameType(FrameType.HORIZONTAL)
                                        .orientation(true)
                                        .boardId(1L)
                                        .boardIdA1(1L)
                                        .boardIdA2(1L)
                                        .boardIdB1(1L)
                                        .boardIdB2(1L)
                                        .edgeIdA1(1L)
                                        .edgeIdA1I(1L)
                                        .edgeIdA2(1L)
                                        .edgeIdA2I(1L)
                                        .edgeIdB1(1L)
                                        .edgeIdB1I(1L)
                                        .edgeIdB2(1L)
                                        .edgeIdB2I(1L)
                                        .dimensionsTOP(new DimensionsData(BigDecimal.valueOf(600), BigDecimal.valueOf(600)))
                                        .dimensionsA1(new DimensionsData(BigDecimal.valueOf(600), BigDecimal.valueOf(100)))
                                        .dimensionsA2(new DimensionsData(BigDecimal.valueOf(600), BigDecimal.valueOf(100)))
                                        .dimensionsB1(new DimensionsData(BigDecimal.valueOf(100), BigDecimal.valueOf(400)))
                                        .dimensionsB2(new DimensionsData(BigDecimal.valueOf(100), BigDecimal.valueOf(400)))
                                        .build())
                                .summary(OrderItemSummaryData.builder()
                                        .partSummary(OrderItemPartSummaryData.builder()
                                                .boardSummary(List.of())
                                                .edgeSummary(List.of())
                                                .gluedArea(BigDecimal.ZERO)
                                                .cutSummary(List.of())
                                                .build())
                                        .totalSummary(OrderItemPartSummaryData.builder()
                                                .boardSummary(List.of())
                                                .edgeSummary(List.of())
                                                .gluedArea(BigDecimal.ZERO)
                                                .cutSummary(List.of())
                                                .build())
                                        .build()
                                ).build()
                )).build();
    }

    @Test
    void orderToHtmlContent_whenOrder_thenTheseResults() {
        final HtmlContentData htmlContentData = orderHtmlUtilService.orderToHtmlContent(applicationPropertyService.getOrderProperties(), order);

        assertThat(htmlContentData.title()).isEqualTo("Order No.001");
        assertThat(htmlContentData.creator()).isEqualTo("mr. Jimbo Tester");
        assertThat(htmlContentData.created()).isEqualTo("01.01.2024 12:00");
        assertThat(htmlContentData.orderNumber()).isEqualTo("001");
        assertThat(htmlContentData.deliveryDate()).isEqualTo("01.01.2024");
        assertThat(htmlContentData.packageType()).isEqualTo("PACKAGE_WITH_REMAINS");

        assertThat(htmlContentData.orderContact()).isNotNull();
        assertThat(htmlContentData.orderContact().name()).isEqualTo("Mr. Wood Pecker");
        assertThat(htmlContentData.orderContact().street()).isEqualTo("Oak alley 12");
        assertThat(htmlContentData.orderContact().zipCode()).isEqualTo("000 00");
        assertThat(htmlContentData.orderContact().city()).isEqualTo("Wooden");
        assertThat(htmlContentData.orderContact().state()).isEqualTo("Forestland");
        assertThat(htmlContentData.orderContact().phone()).isEqualTo("0905 505 505");
        assertThat(htmlContentData.orderContact().email()).isEqualTo("wood.pecker@sekvoi.tree");
        assertThat(htmlContentData.orderContact().businessId()).isEqualTo("B001001");
        assertThat(htmlContentData.orderContact().taxId()).isEqualTo("T001001");

        assertThat(htmlContentData.summary()).isNotNull();

        assertThat(htmlContentData.summary().boardSummary()).hasSize(1);
        assertThat(htmlContentData.summary().boardSummary().getFirst().material()).isEqualTo("material1");
        assertThat(htmlContentData.summary().boardSummary().getFirst().name()).isEqualTo("b-code s-code name");
        assertThat(htmlContentData.summary().boardSummary().getFirst().area()).isEqualTo("4.300 ㎡");
        assertThat(htmlContentData.summary().boardSummary().getFirst().boardsCount()).isEqualTo("2 p");
        assertThat(htmlContentData.summary().boardSummary().getFirst().weight()).isEqualTo("10.240 kg");
        assertThat(htmlContentData.summary().boardSummary().getFirst().price()).isEqualTo("100.00 €");
        assertThat(htmlContentData.summary().boardSummary().getFirst().vatPrice()).isEqualTo("120.00 €");

        assertThat(htmlContentData.summary().edgeSummary()).hasSize(1);
        assertThat(htmlContentData.summary().edgeSummary().getFirst().name()).isEqualTo("code 19x0.8");
        assertThat(htmlContentData.summary().edgeSummary().getFirst().length()).isEqualTo("4.200 m");
        assertThat(htmlContentData.summary().edgeSummary().getFirst().glueLength()).isEqualTo("3.800 m");
        assertThat(htmlContentData.summary().edgeSummary().getFirst().weight()).isEqualTo("0.830 kg");
        assertThat(htmlContentData.summary().edgeSummary().getFirst().edgePrice()).isEqualTo("12.12 €");
        assertThat(htmlContentData.summary().edgeSummary().getFirst().edgeVatPrice()).isEqualTo("15.15 €");
        assertThat(htmlContentData.summary().edgeSummary().getFirst().gluePrice()).isEqualTo("7.54 €");
        assertThat(htmlContentData.summary().edgeSummary().getFirst().glueVatPrice()).isEqualTo("8.56 €");

        assertThat(htmlContentData.summary().glueSummary().area()).isEqualTo("4.350 ㎡");
        assertThat(htmlContentData.summary().glueSummary().price()).isEqualTo("100.00 €");
        assertThat(htmlContentData.summary().glueSummary().vatPrice()).isEqualTo("120.00 €");

        assertThat(htmlContentData.summary().cutSummary()).hasSize(1);
        assertThat(htmlContentData.summary().cutSummary().getFirst().thickness()).isEqualTo("10 mm");
        assertThat(htmlContentData.summary().cutSummary().getFirst().amount()).isEqualTo("6.700 m");
        assertThat(htmlContentData.summary().cutSummary().getFirst().price()).isEqualTo("2.68 €");
        assertThat(htmlContentData.summary().cutSummary().getFirst().vatPrice()).isEqualTo("3.03 €");

        assertThat(htmlContentData.summary().weight()).isEqualTo("123.123 kg");
        assertThat(htmlContentData.summary().total()).isEqualTo("100.00 €");
        assertThat(htmlContentData.summary().vatTotal()).isEqualTo("120.00 €");

        assertThat(htmlContentData.items()).hasSize(4);
        assertThat(htmlContentData.items().getFirst().partNum()).isEqualTo("1");
        assertThat(htmlContentData.items().getFirst().name()).isEqualTo("test0");
        assertThat(htmlContentData.items().getFirst().dimX()).isEqualTo("250 mm");
        assertThat(htmlContentData.items().getFirst().dimY()).isEqualTo("250 mm");
        assertThat(htmlContentData.items().getFirst().quantity()).isEqualTo("1 p");
        assertThat(htmlContentData.items().getFirst().description()).isEqualTo("test part 0");
        assertThat(htmlContentData.items().getFirst().image().startsWith("data:image/png;base64,")).isTrue();
        assertThat(htmlContentData.items().getFirst().boards()).hasSize(1);
        assertThat(htmlContentData.items().getFirst().boards().getFirst().position()).isEqualTo("TOP");
        assertThat(htmlContentData.items().getFirst().boards().getFirst().material()).isEqualTo("material1");
        assertThat(htmlContentData.items().getFirst().boards().getFirst().name()).isEqualTo("b-code s-code name");
        assertThat(htmlContentData.items().getFirst().boards().getFirst().dimX()).isEqualTo("250 mm");
        assertThat(htmlContentData.items().getFirst().boards().getFirst().dimY()).isEqualTo("250 mm");
        assertThat(htmlContentData.items().getFirst().boards().getFirst().image().startsWith("data:image/png;base64,")).isTrue();
        assertThat(htmlContentData.items().getFirst().edges()).hasSize(8);
        assertThat(htmlContentData.items().getFirst().edges().getFirst().position()).isEqualTo("A1");
        assertThat(htmlContentData.items().getFirst().edges().getFirst().name()).isEqualTo("code 19x0.8");
        assertThat(htmlContentData.items().getFirst().edges().get(1).position()).isEqualTo("A2");
        assertThat(htmlContentData.items().getFirst().edges().get(1).name()).isEqualTo("code 19x0.8");
        assertThat(htmlContentData.items().getFirst().edges().get(2).position()).isEqualTo("B1");
        assertThat(htmlContentData.items().getFirst().edges().get(2).name()).isEqualTo("code 19x0.8");
        assertThat(htmlContentData.items().getFirst().edges().get(3).position()).isEqualTo("B2");
        assertThat(htmlContentData.items().getFirst().edges().get(3).name()).isEqualTo("code 19x0.8");
        assertThat(htmlContentData.items().getFirst().edges().get(4).position()).isEqualTo("A1B1");
        assertThat(htmlContentData.items().getFirst().edges().get(4).name()).isEqualTo("code 19x0.8");
        assertThat(htmlContentData.items().getFirst().edges().get(5).position()).isEqualTo("A1B2");
        assertThat(htmlContentData.items().getFirst().edges().get(5).name()).isEqualTo("code 19x0.8");
        assertThat(htmlContentData.items().getFirst().edges().get(6).position()).isEqualTo("A2B1");
        assertThat(htmlContentData.items().getFirst().edges().get(6).name()).isEqualTo("code 19x0.8");
        assertThat(htmlContentData.items().getFirst().edges().getLast().position()).isEqualTo("A2B2");
        assertThat(htmlContentData.items().getFirst().edges().getLast().name()).isEqualTo("code 19x0.8");
        assertThat(htmlContentData.items().getFirst().corners()).hasSize(4);
        assertThat(htmlContentData.items().getFirst().corners().getFirst().position()).isEqualTo("A1B1");
        assertThat(htmlContentData.items().getFirst().corners().getFirst().name()).isEqualTo("80 mm x 100 mm");
        assertThat(htmlContentData.items().getFirst().corners().get(1).position()).isEqualTo("A1B2");
        assertThat(htmlContentData.items().getFirst().corners().get(1).name()).isEqualTo("75 mm x 75 mm");
        assertThat(htmlContentData.items().getFirst().corners().get(2).position()).isEqualTo("A2B1");
        assertThat(htmlContentData.items().getFirst().corners().get(2).name()).isEqualTo("r 100 mm");
        assertThat(htmlContentData.items().getFirst().corners().getLast().position()).isEqualTo("A2B2");
        assertThat(htmlContentData.items().getFirst().corners().getLast().name()).isEqualTo("r 90 mm");
    }

    @Test
    void generateHtml_whenOrder_thenTheseResults() throws IOException {
        Files.write(Path.of("./target").resolve("order.html"), orderHtmlUtilService.generateHtml(order).getBytes());
    }
}
