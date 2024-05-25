package sk.janobono.wiwa.business.impl.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import sk.janobono.wiwa.business.impl.component.DataUtil;
import sk.janobono.wiwa.business.impl.component.MaterialUtil;
import sk.janobono.wiwa.business.model.DimensionsData;
import sk.janobono.wiwa.business.model.application.ManufacturePropertiesData;
import sk.janobono.wiwa.business.model.application.OrderPropertiesData;
import sk.janobono.wiwa.business.model.board.BoardCategoryData;
import sk.janobono.wiwa.business.model.order.OrderBoardData;
import sk.janobono.wiwa.business.model.order.OrderEdgeData;
import sk.janobono.wiwa.business.model.order.part.PartData;
import sk.janobono.wiwa.business.service.ApplicationPropertyService;
import sk.janobono.wiwa.component.ScDf;
import sk.janobono.wiwa.dal.domain.OrderItemDo;
import sk.janobono.wiwa.dal.domain.OrderViewDo;
import sk.janobono.wiwa.dal.repository.OrderItemRepository;
import sk.janobono.wiwa.dal.repository.OrderMaterialRepository;
import sk.janobono.wiwa.model.OrderContent;
import sk.janobono.wiwa.model.OrderFormat;
import sk.janobono.wiwa.model.OrderPackageType;
import sk.janobono.wiwa.model.OrderStatus;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class OrderCsvUtilServiceTest {

    private ObjectMapper objectMapper;

    private OrderCsvUtilService orderCsvUtilService;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderMaterialRepository orderMaterialRepository;

    @Mock
    private ApplicationPropertyService applicationPropertyService;

    @Mock
    private MaterialUtilService materialUtilService;

    private DataUtil dataUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();

        dataUtil = new DataUtil(objectMapper);
        final MaterialUtil materialUtil = new MaterialUtil(dataUtil);

        orderCsvUtilService = new OrderCsvUtilService(
                new ScDf(),
                dataUtil,
                materialUtil,
                orderItemRepository,
                orderMaterialRepository,
                applicationPropertyService,
                materialUtilService
        );

        Mockito.when(applicationPropertyService.getManufactureProperties()).thenReturn(
                new ManufacturePropertiesData(
                        new DimensionsData(BigDecimal.valueOf(50), BigDecimal.valueOf(50)),
                        new DimensionsData(BigDecimal.valueOf(250), BigDecimal.valueOf(60)),
                        new DimensionsData(BigDecimal.valueOf(250), BigDecimal.valueOf(70)),
                        new DimensionsData(BigDecimal.valueOf(250), BigDecimal.valueOf(80)),
                        BigDecimal.valueOf(8),
                        BigDecimal.valueOf(40),
                        BigDecimal.valueOf(10)
                )
        );

        Mockito.when(applicationPropertyService.getOrderProperties()).thenReturn(
                new OrderPropertiesData(
                        Map.of(),
                        Map.of(),
                        Map.of(),
                        Map.of(),
                        Map.of(
                                OrderFormat.CSV_NUMBER, "%d %s",
                                OrderFormat.CSV_BASIC, "%s (basic %s-%dx%dmm-%dp)",
                                OrderFormat.CSV_FRAME, "%s (frame %s-%dx%dmm-%dp)",
                                OrderFormat.CSV_DUPLICATED_BASIC, "%s (duplicated basic %s-%dx%dmm-%dp)",
                                OrderFormat.CSV_DUPLICATED_FRAME, "%s (duplicated frame %s-%dx%dmm-%dp)",
                                OrderFormat.CSV_EDGE, "%s %dx%.1f",
                                OrderFormat.CSV_CORNER_STRAIGHT, "%s %dx%d",
                                OrderFormat.CSV_CORNER_ROUNDED, "%s r%d"
                        ),
                        Map.of(
                                OrderContent.MATERIAL_NOT_FOUND, "Material not found",
                                OrderContent.BOARD_NOT_FOUND, "Board not found",
                                OrderContent.EDGE_NOT_FOUND, "Edge not found"
                        ),
                        Map.of(),
                        ";",
                        Map.of("<.*?>", "", "\\s+", "_"),
                        Map.of()
                )
        );

        final List<OrderBoardData> boards = List.of(
                OrderBoardData.builder().id(1L).boardCode("b1").structureCode("s1").name("n1").length(BigDecimal.valueOf(2000)).width(BigDecimal.valueOf(1500)).thickness(BigDecimal.valueOf(10)).build(),
                OrderBoardData.builder().id(2L).boardCode("b2").structureCode("s2").name("n2").length(BigDecimal.valueOf(2000)).width(BigDecimal.valueOf(1500)).thickness(BigDecimal.valueOf(20)).build(),
                OrderBoardData.builder().id(3L).boardCode("b3").structureCode("s3").name("n3").length(BigDecimal.valueOf(2000)).width(BigDecimal.valueOf(1500)).thickness(BigDecimal.valueOf(30)).build()
        );
        final List<OrderEdgeData> edges = List.of(
                OrderEdgeData.builder().id(1L).code("e1").width(BigDecimal.valueOf(18)).thickness(new BigDecimal("0.8")).build(),
                OrderEdgeData.builder().id(2L).code("e2").width(BigDecimal.valueOf(28)).thickness(new BigDecimal("1.0")).build(),
                OrderEdgeData.builder().id(3L).code("e3").width(BigDecimal.valueOf(38)).thickness(new BigDecimal("2.0")).build()
        );

        Mockito.when(orderMaterialRepository.findAllByOrderId(Mockito.anyLong())).thenReturn(
                Stream.concat(
                        boards.stream().map(b -> materialUtil.toMaterial(1L, b)),
                        edges.stream().map(e -> materialUtil.toMaterial(1L, e))
                ).toList()
        );

        Mockito.when(applicationPropertyService.getBoardMaterialCategory()).thenReturn(
                new BoardCategoryData(1L, "code", "name")
        );

        Mockito.when(materialUtilService.getMaterialNames(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(
                Map.of(1L, "material1", 2L, "material2")
        );

        Mockito.when(materialUtilService.findBoard(Mockito.any(), Mockito.anyLong())).thenCallRealMethod();
        Mockito.when(materialUtilService.findEdge(Mockito.any(), Mockito.anyLong())).thenCallRealMethod();
        Mockito.when(materialUtilService.getDecor(Mockito.any(), Mockito.anyLong(), Mockito.anyString())).thenCallRealMethod();
        Mockito.when(materialUtilService.getEdge(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenCallRealMethod();
    }

    @Test
    void generateCsv_whenPartBasic_thenTheseResults() throws IOException {
        final PartData part = objectMapper.readValue(getClass().getResource("/part_basic.json"), PartData.class);

        Mockito.when(orderItemRepository.findAllByOrderId(Mockito.anyLong())).thenReturn(
                List.of(OrderItemDo.builder()
                        .id(1L)
                        .orderId(1L)
                        .sortNum(0)
                        .name("basic")
                        .description("test basic part")
                        .quantity(1)
                        .part(dataUtil.serializeValue(part))
                        .build())
        );

        final String csv = orderCsvUtilService.generateCsv(new OrderViewDo(
                1L,
                1L,
                LocalDateTime.now(),
                1L,
                LocalDate.now(),
                OrderPackageType.NO_PACKAGE,
                OrderStatus.FINISHED,
                BigDecimal.TEN,
                BigDecimal.TEN
        ));

        assertThat(csv).isEqualTo("""
                "NUMBER";"NAME";"MATERIAL";"DECOR";"X_DIMENSION";"Y_DIMENSION";"QUANTITY";"ORIENTATION";"THICKNESS";"EDGE_A1";"EDGE_A2";"EDGE_B1";"EDGE_B2";"CORNER_A1B1";"CORNER_A1B2";"CORNER_A2B1";"CORNER_A2B2";"DESCRIPTION"
                "1_TOP";"basic_(basic_TOP-500x500mm-1p)";"material1";"b1_s1_n1";500;500;1;0;10;"e1_18x0.8";"e1_18x0.8";"e1_18x0.8";"e1_18x0.8";"A1B1_50x50_e1_18x0.8";"A1B2_50x50_e1_18x0.8";"A2B1_r50_e1_18x0.8";"A2B2_r50_e1_18x0.8";"test_basic_part"
                """);
    }

    @Test
    void generateCsv_whenPartDuplicatedBasic_thenTheseResults() throws IOException {
        final PartData part = objectMapper.readValue(getClass().getResource("/part_duplicated.json"), PartData.class);

        Mockito.when(orderItemRepository.findAllByOrderId(Mockito.anyLong())).thenReturn(
                List.of(OrderItemDo.builder()
                        .id(1L)
                        .orderId(1L)
                        .sortNum(0)
                        .name("basic")
                        .description("test basic part")
                        .quantity(1)
                        .part(dataUtil.serializeValue(part))
                        .build())
        );

        final String csv = orderCsvUtilService.generateCsv(new OrderViewDo(
                1L,
                1L,
                LocalDateTime.now(),
                1L,
                LocalDate.now(),
                OrderPackageType.NO_PACKAGE,
                OrderStatus.FINISHED,
                BigDecimal.TEN,
                BigDecimal.TEN
        ));

        assertThat(csv).isEqualTo("""
                "NUMBER";"NAME";"MATERIAL";"DECOR";"X_DIMENSION";"Y_DIMENSION";"QUANTITY";"ORIENTATION";"THICKNESS";"EDGE_A1";"EDGE_A2";"EDGE_B1";"EDGE_B2";"CORNER_A1B1";"CORNER_A1B2";"CORNER_A2B1";"CORNER_A2B2";"DESCRIPTION"
                "1_TOP";"basic_(duplicated_basic_TOP-500x500mm-1p)";"material1";"b1_s1_n1";520;520;1;1;10;"e3_38x2.0";"e3_38x2.0";"e3_38x2.0";"e3_38x2.0";"A1B1_50x50";"A1B2_50x50";"A2B1_r50";"A2B2_r50";"test_basic_part"
                "1_BOTTOM";"basic_(duplicated_basic_BOTTOM-500x500mm-1p)";"material2";"b2_s2_n2";520;520;1;1;20;;;;;;;;;
                """);
    }

    @Test
    void generateCsv_whenPartFrame_thenTheseResults() throws IOException {
        final PartData part = objectMapper.readValue(getClass().getResource("/part_frame.json"), PartData.class);

        Mockito.when(orderItemRepository.findAllByOrderId(Mockito.anyLong())).thenReturn(
                List.of(OrderItemDo.builder()
                        .id(1L)
                        .orderId(1L)
                        .sortNum(0)
                        .name("frame")
                        .description("test frame part")
                        .quantity(1)
                        .part(dataUtil.serializeValue(part))
                        .build())
        );


        final String csv = orderCsvUtilService.generateCsv(new OrderViewDo(
                1L,
                1L,
                LocalDateTime.now(),
                1L,
                LocalDate.now(),
                OrderPackageType.NO_PACKAGE,
                OrderStatus.FINISHED,
                BigDecimal.TEN,
                BigDecimal.TEN
        ));

        assertThat(csv).isEqualTo("""
                "NUMBER";"NAME";"MATERIAL";"DECOR";"X_DIMENSION";"Y_DIMENSION";"QUANTITY";"ORIENTATION";"THICKNESS";"EDGE_A1";"EDGE_A2";"EDGE_B1";"EDGE_B2";"CORNER_A1B1";"CORNER_A1B2";"CORNER_A2B1";"CORNER_A2B2";"DESCRIPTION"
                "1_A1";"frame_(frame_A1-500x100mm-1p)";"material1";"b1_s1_n1";500;100;1;0;10;"e1_18x0.8";"e1_18x0.8";"e1_18x0.8";"e1_18x0.8";;;;;"test_frame_part"
                "1_A2";"frame_(frame_A2-500x100mm-1p)";"material1";"b1_s1_n1";500;100;1;0;10;"e1_18x0.8";"e1_18x0.8";"e1_18x0.8";"e1_18x0.8";;;;;
                "1_B1";"frame_(frame_B1-100x300mm-1p)";"material1";"b1_s1_n1";100;300;1;0;10;;;"e1_18x0.8";"e1_18x0.8";;;;;
                "1_B2";"frame_(frame_B2-100x300mm-1p)";"material1";"b1_s1_n1";100;300;1;0;10;;;"e1_18x0.8";"e1_18x0.8";;;;;
                """);
    }

    @Test
    void generateCsv_whenDuplicatedFrame_thenTheseResults() throws IOException {
        final PartData part = objectMapper.readValue(getClass().getResource("/part_duplicated_frame.json"), PartData.class);

        Mockito.when(orderItemRepository.findAllByOrderId(Mockito.anyLong())).thenReturn(
                List.of(OrderItemDo.builder()
                        .id(1L)
                        .orderId(1L)
                        .sortNum(0)
                        .name("duplicated frame")
                        .description("test duplicated frame part")
                        .quantity(1)
                        .part(dataUtil.serializeValue(part))
                        .build())
        );

        final String csv = orderCsvUtilService.generateCsv(new OrderViewDo(
                1L,
                1L,
                LocalDateTime.now(),
                1L,
                LocalDate.now(),
                OrderPackageType.NO_PACKAGE,
                OrderStatus.FINISHED,
                BigDecimal.TEN,
                BigDecimal.TEN
        ));

        assertThat(csv).isEqualTo("""
                "NUMBER";"NAME";"MATERIAL";"DECOR";"X_DIMENSION";"Y_DIMENSION";"QUANTITY";"ORIENTATION";"THICKNESS";"EDGE_A1";"EDGE_A2";"EDGE_B1";"EDGE_B2";"CORNER_A1B1";"CORNER_A1B2";"CORNER_A2B1";"CORNER_A2B2";"DESCRIPTION"
                "1_TOP";"duplicated_frame_(duplicated_frame_TOP-500x500mm-1p)";"material1";"b1_s1_n1";520;520;1;1;10;"e2_28x1.0";"e2_28x1.0";"e2_28x1.0";"e2_28x1.0";"A1B1_50x50";"A1B2_50x50";"A2B1_r50";"A2B2_r50";"test_duplicated_frame_part"
                "1_A1";"duplicated_frame_(duplicated_frame_A1-500x100mm-1p)";"material1";"b1_s1_n1";520;110;1;0;10;;"e1_18x0.8";;;;;;;
                "1_A2";"duplicated_frame_(duplicated_frame_A2-500x100mm-1p)";"material1";"b1_s1_n1";520;110;1;0;10;"e1_18x0.8";;;;;;;;
                "1_B1";"duplicated_frame_(duplicated_frame_B1-100x300mm-1p)";"material1";"b1_s1_n1";110;300;1;0;10;;;;"e1_18x0.8";;;;;
                "1_B2";"duplicated_frame_(duplicated_frame_B2-100x300mm-1p)";"material1";"b1_s1_n1";110;300;1;0;10;;;"e1_18x0.8";;;;;;
                """);
    }
}
