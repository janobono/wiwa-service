package sk.janobono.wiwa.business.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;
import sk.janobono.wiwa.business.TestConfigProperties;
import sk.janobono.wiwa.business.TestMail;
import sk.janobono.wiwa.business.TestRepositories;
import sk.janobono.wiwa.business.TestUsers;
import sk.janobono.wiwa.business.impl.component.*;
import sk.janobono.wiwa.business.impl.model.mail.MailData;
import sk.janobono.wiwa.business.impl.util.*;
import sk.janobono.wiwa.business.model.order.*;
import sk.janobono.wiwa.business.model.order.part.PartBasicData;
import sk.janobono.wiwa.business.model.order.part.PartDuplicatedBasicData;
import sk.janobono.wiwa.business.model.order.part.PartDuplicatedFrameData;
import sk.janobono.wiwa.business.model.order.part.PartFrameData;
import sk.janobono.wiwa.business.service.ApplicationPropertyService;
import sk.janobono.wiwa.business.service.OrderService;
import sk.janobono.wiwa.component.ImageUtil;
import sk.janobono.wiwa.component.ScDf;
import sk.janobono.wiwa.config.CommonConfigProperties;
import sk.janobono.wiwa.config.JwtConfigProperties;
import sk.janobono.wiwa.dal.domain.BoardDo;
import sk.janobono.wiwa.dal.domain.CodeListDo;
import sk.janobono.wiwa.dal.domain.CodeListItemDo;
import sk.janobono.wiwa.dal.domain.EdgeDo;
import sk.janobono.wiwa.dal.repository.*;
import sk.janobono.wiwa.model.OrderPackageType;
import sk.janobono.wiwa.model.OrderStatus;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderServiceTest {

    private ObjectMapper objectMapper;
    private TestMail testMail;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();

        final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        final CommonConfigProperties commonConfigProperties = Mockito.mock(CommonConfigProperties.class);
        final JwtConfigProperties jwtConfigProperties = Mockito.mock(JwtConfigProperties.class);
        final TestConfigProperties testConfigProperties = new TestConfigProperties();
        testConfigProperties.mock(commonConfigProperties);
        testConfigProperties.mock(jwtConfigProperties);

        final DataUtil dataUtil = new DataUtil(objectMapper);
        final MaterialUtil materialUtil = new MaterialUtil(dataUtil);
        final PriceUtil priceUtil = new PriceUtil();

        final MailUtilService mailUtilService = Mockito.mock(MailUtilService.class);
        testMail = new TestMail();
        testMail.mock(mailUtilService);

        final ApplicationPropertyRepository applicationPropertyRepository = Mockito.mock(ApplicationPropertyRepository.class);
        final AuthorityRepository authorityRepository = Mockito.mock(AuthorityRepository.class);
        final BoardCodeListItemRepository boardCodeListItemRepository = Mockito.mock(BoardCodeListItemRepository.class);
        final BoardRepository boardRepository = Mockito.mock(BoardRepository.class);
        final CodeListItemRepository codeListItemRepository = Mockito.mock(CodeListItemRepository.class);
        final CodeListRepository codeListRepository = Mockito.mock(CodeListRepository.class);
        final EdgeRepository edgeRepository = Mockito.mock(EdgeRepository.class);
        final OrderCommentRepository orderCommentRepository = Mockito.mock(OrderCommentRepository.class);
        final OrderContactRepository orderContactRepository = Mockito.mock(OrderContactRepository.class);
        final OrderItemRepository orderItemRepository = Mockito.mock(OrderItemRepository.class);
        final OrderItemSummaryRepository orderItemSummaryRepository = Mockito.mock(OrderItemSummaryRepository.class);
        final OrderMaterialRepository orderMaterialRepository = Mockito.mock(OrderMaterialRepository.class);
        final OrderNumberRepository orderNumberRepository = Mockito.mock(OrderNumberRepository.class);
        final OrderRepository orderRepository = Mockito.mock(OrderRepository.class);
        final OrderStatusRepository orderStatusRepository = Mockito.mock(OrderStatusRepository.class);
        final OrderSummaryViewRepository orderSummaryViewRepository = Mockito.mock(OrderSummaryViewRepository.class);
        final OrderViewRepository orderViewRepository = Mockito.mock(OrderViewRepository.class);
        final UserRepository userRepository = Mockito.mock(UserRepository.class);

        final TestRepositories testRepositories = new TestRepositories();
        testRepositories.mock(applicationPropertyRepository);
        testRepositories.mock(authorityRepository);
        testRepositories.mock(boardCodeListItemRepository);
        testRepositories.mock(boardRepository);
        testRepositories.mock(codeListItemRepository);
        testRepositories.mock(codeListRepository);
        testRepositories.mock(edgeRepository);
        testRepositories.mock(orderCommentRepository);
        testRepositories.mock(orderContactRepository);
        testRepositories.mock(orderItemRepository);
        testRepositories.mock(orderItemSummaryRepository);
        testRepositories.mock(orderMaterialRepository);
        testRepositories.mock(orderNumberRepository);
        testRepositories.mock(orderRepository);
        testRepositories.mock(orderStatusRepository);
        testRepositories.mock(orderSummaryViewRepository);
        testRepositories.mock(orderViewRepository);
        testRepositories.mock(userRepository);

        new TestUsers().initUsers(passwordEncoder, userRepository, authorityRepository);

        final SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setApplicationContext(new StaticApplicationContext());
        templateResolver.setPrefix("classpath:/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);

        final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setEnableSpringELCompiler(true);
        templateEngine.setTemplateResolver(templateResolver);

        final ApplicationPropertyService applicationPropertyService = new ApplicationPropertyServiceImpl(
                objectMapper, commonConfigProperties, jwtConfigProperties, new PropertyUtilService(applicationPropertyRepository), codeListRepository
        );

        final MaterialUtilService materialUtilService = new MaterialUtilService(boardCodeListItemRepository);

        final CodeListDo codeList = codeListRepository.save(CodeListDo.builder()
                .code("code")
                .name("materials")
                .build());
        applicationPropertyService.setBoardMaterialCategory(codeList.getId());

        final CodeListItemDo codeListItem1 = codeListItemRepository.save(CodeListItemDo.builder()
                .codeListId(codeList.getId())
                .treeCode("code1")
                .code("code1")
                .value("material1")
                .sortNum(0)
                .build());

        final CodeListItemDo codeListItem2 = codeListItemRepository.save(CodeListItemDo.builder()
                .codeListId(codeList.getId())
                .treeCode("code2")
                .code("code2")
                .value("material2")
                .sortNum(0)
                .build());

        final CodeListItemDo codeListItem3 = codeListItemRepository.save(CodeListItemDo.builder()
                .codeListId(codeList.getId())
                .treeCode("code3")
                .code("code3")
                .value("material3")
                .sortNum(0)
                .build());

        final BoardDo board1 = boardRepository.save(
                BoardDo.builder()
                        .code("code1")
                        .boardCode("board code 1")
                        .structureCode("structure code 1")
                        .name("board 1")
                        .orientation(true)
                        .weight(new BigDecimal("10"))
                        .length(BigDecimal.valueOf(2000))
                        .width(BigDecimal.valueOf(1500))
                        .thickness(BigDecimal.valueOf(10))
                        .price(BigDecimal.valueOf(500))
                        .build()
        );
        boardCodeListItemRepository.saveAll(board1.getId(), List.of(codeListItem1.getId()));

        final BoardDo board2 = boardRepository.save(
                BoardDo.builder()
                        .code("code2")
                        .boardCode("board code 2")
                        .structureCode("structure code 2")
                        .name("board 2")
                        .orientation(false)
                        .weight(new BigDecimal("20"))
                        .length(BigDecimal.valueOf(2000))
                        .width(BigDecimal.valueOf(1500))
                        .thickness(BigDecimal.valueOf(20))
                        .price(BigDecimal.valueOf(750))
                        .build()
        );
        boardCodeListItemRepository.saveAll(board2.getId(), List.of(codeListItem2.getId()));

        final BoardDo board3 = boardRepository.save(
                BoardDo.builder()
                        .code("code3")
                        .boardCode("board code 3")
                        .structureCode("structure code 3")
                        .name("board 3")
                        .orientation(false)
                        .weight(new BigDecimal("30"))
                        .length(BigDecimal.valueOf(2000))
                        .width(BigDecimal.valueOf(1500))
                        .thickness(BigDecimal.valueOf(30))
                        .price(BigDecimal.valueOf(1000))
                        .build()
        );
        boardCodeListItemRepository.saveAll(board3.getId(), List.of(codeListItem3.getId()));

        edgeRepository.save(EdgeDo.builder()
                .code("ecode 1")
                .name("edge 1")
                .weight(new BigDecimal("0.1"))
                .width(BigDecimal.valueOf(18))
                .thickness(new BigDecimal("0.5"))
                .price(new BigDecimal("5.30"))
                .build());

        edgeRepository.save(EdgeDo.builder()
                .code("ecode 2")
                .name("edge 2")
                .weight(new BigDecimal("0.2"))
                .width(BigDecimal.valueOf(28))
                .thickness(new BigDecimal("0.8"))
                .price(new BigDecimal("7.30"))
                .build());

        edgeRepository.save(EdgeDo.builder()
                .code("ecode 3")
                .name("edge 3")
                .weight(new BigDecimal("0.3"))
                .width(BigDecimal.valueOf(38))
                .thickness(new BigDecimal("1.0"))
                .price(new BigDecimal("9.30"))
                .build());

        orderService = new OrderServiceImpl(
                commonConfigProperties,
                dataUtil,
                materialUtil,
                priceUtil,
                new SummaryUtil(new OrderSummaryCalculationUtil(priceUtil), new OrderSummaryCodeMapper()),
                boardRepository,
                edgeRepository,
                orderRepository,
                orderCommentRepository,
                orderContactRepository,
                orderItemRepository,
                orderItemSummaryRepository,
                orderMaterialRepository,
                orderNumberRepository,
                orderStatusRepository,
                orderSummaryViewRepository,
                orderViewRepository,
                mailUtilService,
                new OrderCsvUtilService(new ScDf(), dataUtil, materialUtil, orderItemRepository, orderMaterialRepository, applicationPropertyService, materialUtilService),
                new OrderHtmlUtilService(commonConfigProperties, templateEngine, new ImageUtil(), applicationPropertyService, materialUtilService),
                new UserUtilService(passwordEncoder, authorityRepository, userRepository),
                applicationPropertyService
        );
    }

    @Test
    void fullTest() throws IOException {
        final long USER_ID = 4L;

        OrderData order = orderService.addOrder(USER_ID);
        assertThat(order).isNotNull();
        assertThat(order.id()).isEqualTo(1L);
        assertThat(order.creator()).isNotNull();
        assertThat(order.orderNumber()).isEqualTo(1);
        assertThat(order.status()).isEqualTo(OrderStatus.NEW);

        order = orderService.addItem(order.id(), USER_ID, OrderItemChangeData.builder()
                .name("basic")
                .description("Part basic item.")
                .quantity(1)
                .part(objectMapper.readValue(getClass().getResource("/part_basic.json"), PartBasicData.class))
                .build(), false);
        assertThat(order).isNotNull();
        assertThat(order.items()).hasSize(1);

        order = orderService.addItem(order.id(), USER_ID, OrderItemChangeData.builder()
                .name("duplicated basic")
                .description("Part duplicated basic item.")
                .quantity(1)
                .part(objectMapper.readValue(getClass().getResource("/part_duplicated.json"), PartDuplicatedBasicData.class))
                .build(), false);
        assertThat(order).isNotNull();
        assertThat(order.items()).hasSize(2);

        order = orderService.addItem(order.id(), USER_ID, OrderItemChangeData.builder()
                .name("duplicated frame")
                .description("Part duplicated frame item.")
                .quantity(1)
                .part(objectMapper.readValue(getClass().getResource("/part_duplicated_frame.json"), PartDuplicatedFrameData.class))
                .build(), false);
        assertThat(order).isNotNull();
        assertThat(order.items()).hasSize(3);

        order = orderService.addItem(order.id(), USER_ID, OrderItemChangeData.builder()
                .name("frame")
                .description("Part frame item.")
                .quantity(1)
                .part(objectMapper.readValue(getClass().getResource("/part_frame.json"), PartFrameData.class))
                .build(), false);
        assertThat(order).isNotNull();
        assertThat(order.items()).hasSize(4);

        order = orderService.sendOrder(order.id(), USER_ID, SendOrderData.builder()
                .contact(OrderContactData.builder()
                        .name("John Doe")
                        .street("Somewhere 20")
                        .zipCode("000 00")
                        .city("Data city")
                        .state("Data state")
                        .phone("00 000 000")
                        .email("john.doe@data.com")
                        .build())
                .gdprAgreement(true)
                .businessConditionsAgreement(true)
                .packageType(OrderPackageType.NO_PACKAGE)
                .build());
        assertThat(order).isNotNull();
        assertThat(order.status()).isEqualTo(OrderStatus.SENT);
        final MailData mailData = testMail.getMail();
        assertThat(mailData).isNotNull();
        assertThat(mailData.content()).isNotNull();
        assertThat(mailData.content().title()).isEqualTo("Order send - order No.001");

        order = orderService.addComment(order.id(), USER_ID, new OrderCommentChangeData("comment"));
        assertThat(order).isNotNull();
        assertThat(order.comments()).hasSize(1);

        assertThat(orderService.getCsv(order.id())).isEqualTo("""
                "NUMBER";"NAME";"MATERIAL";"DECOR";"X_DIMENSION";"Y_DIMENSION";"QUANTITY";"ORIENTATION";"THICKNESS";"EDGE_A1";"EDGE_A2";"EDGE_B1";"EDGE_B2";"CORNER_A1B1";"CORNER_A1B2";"CORNER_A2B1";"CORNER_A2B2";"DESCRIPTION"
                "1_TOP";"basic_(basic_TOP-500x500mm-1p)";"material1";"board_code_1_structure_code_1_board_1";500;500;1;0;10;"ecode_1_18x0.5";"ecode_1_18x0.5";"ecode_1_18x0.5";"ecode_1_18x0.5";"A1B1_50x50_ecode_1_18x0.5";"A1B2_50x50_ecode_1_18x0.5";"A2B1_r50_ecode_1_18x0.5";"A2B2_r50_ecode_1_18x0.5";"Part_basic_item."
                "2_TOP";"duplicated_basic_(duplicated_basic_TOP-500x500mm-1p)";"material1";"board_code_1_structure_code_1_board_1";520;520;1;1;10;"ecode_3_38x1.0";"ecode_3_38x1.0";"ecode_3_38x1.0";"ecode_3_38x1.0";"A1B1_50x50";"A1B2_50x50";"A2B1_r50";"A2B2_r50";"Part_duplicated_basic_item."
                "2_BOTTOM";"duplicated_basic_(duplicated_basic_BOTTOM-500x500mm-1p)";"material2";"board_code_2_structure_code_2_board_2";520;520;1;1;20;;;;;;;;;
                "3_TOP";"duplicated_frame_(duplicated_frame_TOP-500x500mm-1p)";"material1";"board_code_1_structure_code_1_board_1";520;520;1;1;10;"ecode_2_28x0.8";"ecode_2_28x0.8";"ecode_2_28x0.8";"ecode_2_28x0.8";"A1B1_50x50";"A1B2_50x50";"A2B1_r50";"A2B2_r50";"Part_duplicated_frame_item."
                "3_A1";"duplicated_frame_(duplicated_frame_A1-500x100mm-1p)";"material1";"board_code_1_structure_code_1_board_1";520;110;1;0;10;;"ecode_1_18x0.5";;;;;;;
                "3_A2";"duplicated_frame_(duplicated_frame_A2-500x100mm-1p)";"material1";"board_code_1_structure_code_1_board_1";520;110;1;0;10;"ecode_1_18x0.5";;;;;;;;
                "3_B1";"duplicated_frame_(duplicated_frame_B1-100x300mm-1p)";"material1";"board_code_1_structure_code_1_board_1";110;300;1;0;10;;;;"ecode_1_18x0.5";;;;;
                "3_B2";"duplicated_frame_(duplicated_frame_B2-100x300mm-1p)";"material1";"board_code_1_structure_code_1_board_1";110;300;1;0;10;;;"ecode_1_18x0.5";;;;;;
                "4_A1";"frame_(frame_A1-500x100mm-1p)";"material1";"board_code_1_structure_code_1_board_1";500;100;1;0;10;"ecode_1_18x0.5";"ecode_1_18x0.5";"ecode_1_18x0.5";"ecode_1_18x0.5";;;;;"Part_frame_item."
                "4_A2";"frame_(frame_A2-500x100mm-1p)";"material1";"board_code_1_structure_code_1_board_1";500;100;1;0;10;"ecode_1_18x0.5";"ecode_1_18x0.5";"ecode_1_18x0.5";"ecode_1_18x0.5";;;;;
                "4_B1";"frame_(frame_B1-100x300mm-1p)";"material1";"board_code_1_structure_code_1_board_1";100;300;1;0;10;;;"ecode_1_18x0.5";"ecode_1_18x0.5";;;;;
                "4_B2";"frame_(frame_B2-100x300mm-1p)";"material1";"board_code_1_structure_code_1_board_1";100;300;1;0;10;;;"ecode_1_18x0.5";"ecode_1_18x0.5";;;;;
                """);

        assertThat(orderService.getHtml(order.id())).isNotBlank();
    }
}
