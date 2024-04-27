package sk.janobono.wiwa.business.impl.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.janobono.wiwa.business.model.DimensionsData;
import sk.janobono.wiwa.business.model.application.ManufacturePropertiesData;
import sk.janobono.wiwa.business.model.order.OrderBoardData;
import sk.janobono.wiwa.business.model.order.OrderEdgeData;
import sk.janobono.wiwa.business.model.order.part.*;
import sk.janobono.wiwa.exception.ApplicationException;
import sk.janobono.wiwa.exception.WiwaException;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PartUtilTest {

    private ObjectMapper objectMapper;

    private Map<Long, OrderBoardData> boards;
    private Map<Long, OrderEdgeData> edges;
    private ManufacturePropertiesData manufactureProperties;

    @BeforeEach
    void setUp() {
        objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();

        boards = Map.of(
                1L, OrderBoardData.builder().length(BigDecimal.valueOf(2000)).width(BigDecimal.valueOf(1500)).thickness(BigDecimal.valueOf(10)).build(),
                2L, OrderBoardData.builder().length(BigDecimal.valueOf(2000)).width(BigDecimal.valueOf(1500)).thickness(BigDecimal.valueOf(20)).build(),
                3L, OrderBoardData.builder().length(BigDecimal.valueOf(2000)).width(BigDecimal.valueOf(1500)).thickness(BigDecimal.valueOf(30)).build()
        );
        edges = Map.of(
                1L, OrderEdgeData.builder().width(BigDecimal.valueOf(18)).build(),
                2L, OrderEdgeData.builder().width(BigDecimal.valueOf(28)).build(),
                3L, OrderEdgeData.builder().width(BigDecimal.valueOf(38)).build()
        );
        manufactureProperties = new ManufacturePropertiesData(
                new DimensionsData(BigDecimal.valueOf(50), BigDecimal.valueOf(50)),
                new DimensionsData(BigDecimal.valueOf(250), BigDecimal.valueOf(60)),
                new DimensionsData(BigDecimal.valueOf(250), BigDecimal.valueOf(70)),
                new DimensionsData(BigDecimal.valueOf(250), BigDecimal.valueOf(80)),
                BigDecimal.valueOf(8),
                BigDecimal.valueOf(40),
                BigDecimal.valueOf(10)
        );
    }

    @Test
    void boards_whenVariousBoards_thenTheseResults() {
        Map<BoardPosition, Long> boards = PartBasicData.builder()
                .boardId(1L)
                .build()
                .boards();
        assertThat(boards.get(BoardPosition.TOP)).isEqualTo(1L);
        assertThat(boards.size()).isEqualTo(1);

        boards = PartFrameData.builder()
                .boardIdA1(1L)
                .boardIdA2(2L)
                .boardIdB1(3L)
                .boardIdB2(4L)
                .build()
                .boards();
        assertThat(boards.get(BoardPosition.A1)).isEqualTo(1L);
        assertThat(boards.get(BoardPosition.A2)).isEqualTo(2L);
        assertThat(boards.get(BoardPosition.B1)).isEqualTo(3L);
        assertThat(boards.get(BoardPosition.B2)).isEqualTo(4L);
        assertThat(boards.size()).isEqualTo(4);

        boards = PartDuplicatedBasicData.builder()
                .boardId(1L)
                .boardIdBottom(2L)
                .build()
                .boards();
        assertThat(boards.get(BoardPosition.TOP)).isEqualTo(1L);
        assertThat(boards.get(BoardPosition.BOTTOM)).isEqualTo(2L);
        assertThat(boards.size()).isEqualTo(2);

        boards = PartDuplicatedFrameData.builder()
                .boardId(1L)
                .boardIdA1(2L)
                .boardIdA2(3L)
                .boardIdB1(4L)
                .boardIdB2(5L)
                .build()
                .boards();
        assertThat(boards.get(BoardPosition.TOP)).isEqualTo(1L);
        assertThat(boards.get(BoardPosition.A1)).isEqualTo(2L);
        assertThat(boards.get(BoardPosition.A2)).isEqualTo(3L);
        assertThat(boards.get(BoardPosition.B1)).isEqualTo(4L);
        assertThat(boards.get(BoardPosition.B2)).isEqualTo(5L);
        assertThat(boards.size()).isEqualTo(5);
    }

    @Test
    void edges_whenVariousBoards_thenTheseResults() {
        Map<EdgePosition, Long> edges = PartBasicData.builder()
                .edgeIdA1(1L)
                .edgeIdA2(2L)
                .edgeIdB1(3L)
                .edgeIdB2(4L)
                .build()
                .edges();
        assertThat(edges.get(EdgePosition.A1)).isEqualTo(1L);
        assertThat(edges.get(EdgePosition.A2)).isEqualTo(2L);
        assertThat(edges.get(EdgePosition.B1)).isEqualTo(3L);
        assertThat(edges.get(EdgePosition.B2)).isEqualTo(4L);
        assertThat(edges.size()).isEqualTo(4);

        edges = PartFrameData.builder()
                .edgeIdA1(1L)
                .edgeIdA1I(2L)
                .edgeIdA2(3L)
                .edgeIdA2I(4L)
                .edgeIdB1(5L)
                .edgeIdB1I(6L)
                .edgeIdB2(7L)
                .edgeIdB2I(8L)
                .build()
                .edges();
        assertThat(edges.get(EdgePosition.A1)).isEqualTo(1L);
        assertThat(edges.get(EdgePosition.A1I)).isEqualTo(2L);
        assertThat(edges.get(EdgePosition.A2)).isEqualTo(3L);
        assertThat(edges.get(EdgePosition.A2I)).isEqualTo(4L);
        assertThat(edges.get(EdgePosition.B1)).isEqualTo(5L);
        assertThat(edges.get(EdgePosition.B1I)).isEqualTo(6L);
        assertThat(edges.get(EdgePosition.B2)).isEqualTo(7L);
        assertThat(edges.get(EdgePosition.B2I)).isEqualTo(8L);
        assertThat(edges.size()).isEqualTo(8);

        edges = PartDuplicatedBasicData.builder()
                .edgeIdA1(1L)
                .edgeIdA2(2L)
                .edgeIdB1(3L)
                .edgeIdB2(4L)
                .build()
                .edges();
        assertThat(edges.get(EdgePosition.A1)).isEqualTo(1L);
        assertThat(edges.get(EdgePosition.A2)).isEqualTo(2L);
        assertThat(edges.get(EdgePosition.B1)).isEqualTo(3L);
        assertThat(edges.get(EdgePosition.B2)).isEqualTo(4L);
        assertThat(edges.size()).isEqualTo(4);

        edges = PartDuplicatedFrameData.builder()
                .edgeIdA1(1L)
                .edgeIdA1I(2L)
                .edgeIdA2(3L)
                .edgeIdA2I(4L)
                .edgeIdB1(5L)
                .edgeIdB1I(6L)
                .edgeIdB2(7L)
                .edgeIdB2I(8L)
                .build()
                .edges();
        assertThat(edges.get(EdgePosition.A1)).isEqualTo(1L);
        assertThat(edges.get(EdgePosition.A1I)).isEqualTo(2L);
        assertThat(edges.get(EdgePosition.A2)).isEqualTo(3L);
        assertThat(edges.get(EdgePosition.A2I)).isEqualTo(4L);
        assertThat(edges.get(EdgePosition.B1)).isEqualTo(5L);
        assertThat(edges.get(EdgePosition.B1I)).isEqualTo(6L);
        assertThat(edges.get(EdgePosition.B2)).isEqualTo(7L);
        assertThat(edges.get(EdgePosition.B2I)).isEqualTo(8L);
        assertThat(edges.size()).isEqualTo(8);
    }

    @Test
    void corners_whenVariousBoards_thenTheseResults() {
        Map<CornerPosition, DimensionsData> corners = PartBasicData.builder()
                .cornerA1B1(new PartCornerRoundedData(BigDecimal.ZERO))
                .cornerA1B2(new PartCornerRoundedData(BigDecimal.ONE))
                .cornerA2B1(new PartCornerRoundedData(BigDecimal.TWO))
                .cornerA2B2(new PartCornerRoundedData(BigDecimal.TEN))
                .build()
                .corners();
        assertThat(corners.get(CornerPosition.A1B1)).isEqualTo(new DimensionsData(BigDecimal.ZERO, BigDecimal.ZERO));
        assertThat(corners.get(CornerPosition.A1B2)).isEqualTo(new DimensionsData(BigDecimal.ONE, BigDecimal.ONE));
        assertThat(corners.get(CornerPosition.A2B1)).isEqualTo(new DimensionsData(BigDecimal.TWO, BigDecimal.TWO));
        assertThat(corners.get(CornerPosition.A2B2)).isEqualTo(new DimensionsData(BigDecimal.TEN, BigDecimal.TEN));
        assertThat(corners.size()).isEqualTo(4);

        corners = PartFrameData.builder()
                .build()
                .corners();
        assertThat(corners.isEmpty()).isTrue();

        corners = PartDuplicatedBasicData.builder()
                .cornerA1B1(new PartCornerRoundedData(BigDecimal.ZERO))
                .cornerA1B2(new PartCornerRoundedData(BigDecimal.ONE))
                .cornerA2B1(new PartCornerRoundedData(BigDecimal.TWO))
                .cornerA2B2(new PartCornerRoundedData(BigDecimal.TEN))
                .build()
                .corners();
        assertThat(corners.get(CornerPosition.A1B1)).isEqualTo(new DimensionsData(BigDecimal.ZERO, BigDecimal.ZERO));
        assertThat(corners.get(CornerPosition.A1B2)).isEqualTo(new DimensionsData(BigDecimal.ONE, BigDecimal.ONE));
        assertThat(corners.get(CornerPosition.A2B1)).isEqualTo(new DimensionsData(BigDecimal.TWO, BigDecimal.TWO));
        assertThat(corners.get(CornerPosition.A2B2)).isEqualTo(new DimensionsData(BigDecimal.TEN, BigDecimal.TEN));
        assertThat(corners.size()).isEqualTo(4);

        corners = PartDuplicatedFrameData.builder()
                .cornerA1B1(new PartCornerRoundedData(BigDecimal.ZERO))
                .cornerA1B2(new PartCornerRoundedData(BigDecimal.ONE))
                .cornerA2B1(new PartCornerRoundedData(BigDecimal.TWO))
                .cornerA2B2(new PartCornerRoundedData(BigDecimal.TEN))
                .build()
                .corners();
        assertThat(corners.get(CornerPosition.A1B1)).isEqualTo(new DimensionsData(BigDecimal.ZERO, BigDecimal.ZERO));
        assertThat(corners.get(CornerPosition.A1B2)).isEqualTo(new DimensionsData(BigDecimal.ONE, BigDecimal.ONE));
        assertThat(corners.get(CornerPosition.A2B1)).isEqualTo(new DimensionsData(BigDecimal.TWO, BigDecimal.TWO));
        assertThat(corners.get(CornerPosition.A2B2)).isEqualTo(new DimensionsData(BigDecimal.TEN, BigDecimal.TEN));
        assertThat(corners.size()).isEqualTo(4);
    }

    @Test
    void validate_whenValidData_thenTheseResults() throws IOException {
        final PartUtil partUtil = new PartUtil();

        PartData part = objectMapper.readValue(getClass().getResource("/part_basic.json"), PartData.class);
        partUtil.validate(part, boards, edges, manufactureProperties);

        part = objectMapper.readValue(getClass().getResource("/part_duplicated.json"), PartData.class);
        partUtil.validate(part, boards, edges, manufactureProperties);

        part = objectMapper.readValue(getClass().getResource("/part_frame.json"), PartData.class);
        partUtil.validate(part, boards, edges, manufactureProperties);

        part = objectMapper.readValue(getClass().getResource("/part_duplicated_frame.json"), PartData.class);
        partUtil.validate(part, boards, edges, manufactureProperties);
    }


//    ApplicationException applicationException = assertThrows(ApplicationException.class,
//            () -> partUtil.validate(PartBasicData.builder()
//                    .rotate(true)
//                    .boardId(1L)
//                    .dimensions(new DimensionsData(BigDecimal.valueOf(2001), BigDecimal.valueOf(1501)))
//                    .build(), boards, edges, manufactureProperties)
//    );
//    assertEquals("Invalid dimensions [2,001,1,501] maximum is ManufactureDimensionsData[x=2000, y=1500]", applicationException.getMessage());
//    assertEquals(WiwaException.ORDER_ITEM_PART_DIMENSION.name(), applicationException.getCode());
//
//    applicationException = assertThrows(ApplicationException.class,
//                () -> partUtil.validate(PartBasicData.builder()
//            .boardId(1L)
//                        .dimensions(new DimensionsData(BigDecimal.valueOf(49), BigDecimal.valueOf(350)))
//            .build(), boards, edges, manufactureProperties)
//            );
//    assertEquals("Invalid dimensions [49,350] minimum is ManufactureDimensionsData[x=50, y=50]", applicationException.getMessage());
//    assertEquals(WiwaException.ORDER_ITEM_PART_DIMENSION.name(), applicationException.getCode());
//
//    applicationException = assertThrows(ApplicationException.class,
//                () -> partUtil.validate(PartBasicData.builder()
//            .boardId(2L)
//                        .edgeIdA1(1L)
//                        .dimensions(new DimensionsData(BigDecimal.valueOf(350), BigDecimal.valueOf(350)))
//            .build(), boards, edges, manufactureProperties)
//            );
//    assertEquals("Invalid edge width [18] minimum is 28", applicationException.getMessage());
//    assertEquals(WiwaException.ORDER_ITEM_PART_EDGE_WIDTH.name(), applicationException.getCode());
//
//    applicationException = assertThrows(ApplicationException.class,
//                () -> partUtil.validate(PartBasicData.builder()
//            .boardId(1L)
//                        .dimensions(new DimensionsData(BigDecimal.valueOf(350), BigDecimal.valueOf(350)))
//            .cornerA1B1(new PartCornerStraightData(new DimensionsData(BigDecimal.valueOf(351), BigDecimal.valueOf(50))))
//            .build(), boards, edges, manufactureProperties)
//            );
//    assertEquals("Invalid corner dimensions", applicationException.getMessage());
//    assertEquals(WiwaException.ORDER_ITEM_PART_CORNER_DIMENSION.name(), applicationException.getCode());
//
//    applicationException = assertThrows(ApplicationException.class,
//                () -> partUtil.validate(PartBasicData.builder()
//            .boardId(1L)
//                        .dimensions(new DimensionsData(BigDecimal.valueOf(350), BigDecimal.valueOf(350)))
//            .cornerA1B1(new PartCornerStraightData(new DimensionsData(BigDecimal.valueOf(50), BigDecimal.valueOf(351))))
//            .build(), boards, edges, manufactureProperties)
//            );
//    assertEquals("Invalid corner dimensions", applicationException.getMessage());
//    assertEquals(WiwaException.ORDER_ITEM_PART_CORNER_DIMENSION.name(), applicationException.getCode());
//
//    applicationException = assertThrows(ApplicationException.class,
//                () -> partUtil.validate(PartBasicData.builder()
//            .boardId(1L)
//                        .dimensions(new DimensionsData(BigDecimal.valueOf(350), BigDecimal.valueOf(350)))
//            .cornerA1B1(new PartCornerStraightData(new DimensionsData(BigDecimal.valueOf(176), BigDecimal.valueOf(50))))
//            .cornerA1B2(new PartCornerStraightData(new DimensionsData(BigDecimal.valueOf(175), BigDecimal.valueOf(50))))
//            .build(), boards, edges, manufactureProperties)
//            );
//    assertEquals("Invalid corner dimensions", applicationException.getMessage());
//    assertEquals(WiwaException.ORDER_ITEM_PART_CORNER_DIMENSION.name(), applicationException.getCode());

//    @Test
//    void validatePartFrame_whenVariousBoards_thenTheseResults() {
//        final PartUtil partUtil = new PartUtil();
//
//        partUtil.validate(PartFrameData.builder()
//                .boardIdA1(1L)
//                .boardIdA2(1L)
//                .boardIdB1(1L)
//                .boardIdB2(1L)
//                .edgeIdA1(1L)
//                .edgeIdA1I(1L)
//                .edgeIdA2(1L)
//                .edgeIdA2I(1L)
//                .edgeIdB1(1L)
//                .edgeIdB1I(1L)
//                .edgeIdB2(1L)
//                .edgeIdB2I(1L)
//                .dimensions(new DimensionsData(BigDecimal.valueOf(350), BigDecimal.valueOf(350)))
//                .dimensionsA1(new DimensionsData(BigDecimal.valueOf(350), BigDecimal.valueOf(55)))
//                .dimensionsA2(new DimensionsData(BigDecimal.valueOf(350), BigDecimal.valueOf(65)))
//                .dimensionsB1(new DimensionsData(BigDecimal.valueOf(55), BigDecimal.valueOf(330)))
//                .dimensionsB2(new DimensionsData(BigDecimal.valueOf(65), BigDecimal.valueOf(330)))
//                .build(), boards, edges, manufactureProperties);
//
//        ApplicationException applicationException = assertThrows(ApplicationException.class,
//                () -> partUtil.validate(PartFrameData.builder()
//                        .boardIdA1(1L)
//                        .boardIdA2(1L)
//                        .boardIdB1(1L)
//                        .boardIdB2(1L)
//                        .dimensions(new DimensionsData(BigDecimal.valueOf(2001), BigDecimal.valueOf(1501)))
//                        .dimensionsA1(new DimensionsData(BigDecimal.valueOf(2001), BigDecimal.valueOf(49)))
//                        .dimensionsA2(new DimensionsData(BigDecimal.valueOf(2001), BigDecimal.valueOf(65)))
//                        .dimensionsB1(new DimensionsData(BigDecimal.valueOf(49), BigDecimal.valueOf(1501)))
//                        .dimensionsB2(new DimensionsData(BigDecimal.valueOf(65), BigDecimal.valueOf(1501)))
//                        .build(), boards, edges, manufactureProperties)
//        );
//        assertEquals("Invalid dimensions [2,001,49] maximum is ManufactureDimensionsData[x=2000, y=1500]", applicationException.getMessage());
//        assertEquals(WiwaException.ORDER_ITEM_PART_DIMENSION.name(), applicationException.getCode());
//
//        applicationException = assertThrows(ApplicationException.class,
//                () -> partUtil.validate(PartFrameData.builder()
//                        .boardIdA1(1L)
//                        .boardIdA2(1L)
//                        .boardIdB1(1L)
//                        .boardIdB2(1L)
//                        .dimensionA(BigDecimal.valueOf(100))
//                        .dimensionB(BigDecimal.valueOf(100))
//                        .dimensionA1B(BigDecimal.valueOf(49))
//                        .dimensionA2B(BigDecimal.valueOf(50))
//                        .dimensionB1A(BigDecimal.valueOf(50))
//                        .dimensionB2A(BigDecimal.valueOf(50))
//                        .build(), boards, edges, manufactureProperties)
//        );
//        assertEquals("Invalid dimensions [100,49] minimum is ManufactureDimensionsData[x=50, y=50]", applicationException.getMessage());
//        assertEquals(WiwaException.ORDER_ITEM_PART_DIMENSION.name(), applicationException.getCode());
//
//        applicationException = assertThrows(ApplicationException.class,
//                () -> partUtil.validate(PartFrameData.builder()
//                        .boardIdA1(1L)
//                        .boardIdA2(2L)
//                        .boardIdB1(1L)
//                        .boardIdB2(1L)
//                        .horizontal(true)
//                        .dimensionA(BigDecimal.valueOf(100))
//                        .dimensionB(BigDecimal.valueOf(100))
//                        .dimensionA1B(BigDecimal.valueOf(50))
//                        .dimensionA2B(BigDecimal.valueOf(50))
//                        .dimensionB1A(BigDecimal.valueOf(50))
//                        .dimensionB2A(BigDecimal.valueOf(50))
//                        .build(), boards, edges, manufactureProperties)
//        );
//        assertEquals("Invalid board thickness", applicationException.getMessage());
//        assertEquals(WiwaException.ORDER_ITEM_PART_THICKNESS.name(), applicationException.getCode());
//    }
//
//    @Test
//    void validatePartDuplicatedBasic_whenVariousBoards_thenTheseResults() {
//        final PartUtil partUtil = new PartUtil();
//
//        partUtil.validate(PartDuplicatedBasicData.builder()
//                .boardIdTop(1L)
//                .boardIdBottom(2L)
//                .edgeIdA1(3L)
//                .edgeIdA2(3L)
//                .edgeIdB1(3L)
//                .edgeIdB2(3L)
//                .dimensionA(BigDecimal.valueOf(350))
//                .dimensionB(BigDecimal.valueOf(350))
//                .cornerA1B1(new PartCornerStraightData(BigDecimal.valueOf(175), BigDecimal.valueOf(30)))
//                .cornerA1B2(new PartCornerStraightData(BigDecimal.valueOf(175), BigDecimal.valueOf(50)))
//                .cornerA2B1(new PartCornerRoundedData(BigDecimal.valueOf(30)))
//                .cornerA2B2(new PartCornerRoundedData(BigDecimal.valueOf(50)))
//                .build(), boards, edges, manufactureProperties);
//
//        ApplicationException applicationException = assertThrows(ApplicationException.class,
//                () -> partUtil.validate(PartDuplicatedBasicData.builder()
//                        .boardIdTop(1L)
//                        .boardIdBottom(2L)
//                        .dimensionA(BigDecimal.valueOf(2001))
//                        .dimensionB(BigDecimal.valueOf(1501))
//                        .build(), boards, edges, manufactureProperties)
//        );
//        assertEquals("Invalid dimensions [2,011,1,511] maximum is ManufactureDimensionsData[x=2000, y=1500]", applicationException.getMessage());
//        assertEquals(WiwaException.ORDER_ITEM_PART_DIMENSION.name(), applicationException.getCode());
//
//        applicationException = assertThrows(ApplicationException.class,
//                () -> partUtil.validate(PartDuplicatedBasicData.builder()
//                        .boardIdTop(1L)
//                        .boardIdBottom(2L)
//                        .dimensionA(BigDecimal.valueOf(49))
//                        .dimensionB(BigDecimal.valueOf(350))
//                        .build(), boards, edges, manufactureProperties)
//        );
//        assertEquals("Invalid dimensions [49,350] minimum is ManufactureDimensionsData[x=50, y=50]", applicationException.getMessage());
//        assertEquals(WiwaException.ORDER_ITEM_PART_DIMENSION.name(), applicationException.getCode());
//
//        applicationException = assertThrows(ApplicationException.class,
//                () -> partUtil.validate(PartDuplicatedBasicData.builder()
//                        .boardIdTop(1L)
//                        .boardIdBottom(2L)
//                        .edgeIdA1(1L)
//                        .dimensionA(BigDecimal.valueOf(350))
//                        .dimensionB(BigDecimal.valueOf(350))
//                        .build(), boards, edges, manufactureProperties)
//        );
//        assertEquals("Invalid edge width [18] minimum is 38", applicationException.getMessage());
//        assertEquals(WiwaException.ORDER_ITEM_PART_EDGE_WIDTH.name(), applicationException.getCode());
//
//        applicationException = assertThrows(ApplicationException.class,
//                () -> partUtil.validate(PartDuplicatedBasicData.builder()
//                        .boardIdTop(1L)
//                        .boardIdBottom(2L)
//                        .dimensionA(BigDecimal.valueOf(350))
//                        .dimensionB(BigDecimal.valueOf(350))
//                        .cornerA1B1(new PartCornerStraightData(BigDecimal.valueOf(351), BigDecimal.valueOf(50)))
//                        .build(), boards, edges, manufactureProperties)
//        );
//        assertEquals("Invalid corner dimensions", applicationException.getMessage());
//        assertEquals(WiwaException.ORDER_ITEM_PART_CORNER_DIMENSION.name(), applicationException.getCode());
//
//        applicationException = assertThrows(ApplicationException.class,
//                () -> partUtil.validate(PartDuplicatedBasicData.builder()
//                        .boardIdTop(1L)
//                        .boardIdBottom(2L)
//                        .dimensionA(BigDecimal.valueOf(350))
//                        .dimensionB(BigDecimal.valueOf(350))
//                        .cornerA1B1(new PartCornerStraightData(BigDecimal.valueOf(50), BigDecimal.valueOf(351)))
//                        .build(), boards, edges, manufactureProperties)
//        );
//        assertEquals("Invalid corner dimensions", applicationException.getMessage());
//        assertEquals(WiwaException.ORDER_ITEM_PART_CORNER_DIMENSION.name(), applicationException.getCode());
//
//        applicationException = assertThrows(ApplicationException.class,
//                () -> partUtil.validate(PartDuplicatedBasicData.builder()
//                        .boardIdTop(1L)
//                        .boardIdBottom(2L)
//                        .dimensionA(BigDecimal.valueOf(350))
//                        .dimensionB(BigDecimal.valueOf(350))
//                        .cornerA1B1(new PartCornerStraightData(BigDecimal.valueOf(176), BigDecimal.valueOf(50)))
//                        .cornerA1B2(new PartCornerStraightData(BigDecimal.valueOf(175), BigDecimal.valueOf(50)))
//                        .build(), boards, edges, manufactureProperties)
//        );
//        assertEquals("Invalid corner dimensions", applicationException.getMessage());
//        assertEquals(WiwaException.ORDER_ITEM_PART_CORNER_DIMENSION.name(), applicationException.getCode());
//    }
//
//    @Test
//    void validatePartDuplicatedFrame_whenVariousBoards_thenTheseResults() {
//        final PartUtil partUtil = new PartUtil();
//
//        partUtil.validate(PartDuplicatedFrameData.builder()
//                .boardId(1L)
//                .boardIdA1(1L)
//                .boardIdA2(1L)
//                .boardIdB1(1L)
//                .boardIdB2(1L)
//                .edgeIdA1(2L)
//                .edgeIdA1IBottom(1L)
//                .edgeIdA2(2L)
//                .edgeIdA2IBottom(1L)
//                .edgeIdB1(2L)
//                .edgeIdB1IBottom(1L)
//                .edgeIdB2(2L)
//                .edgeIdB2IBottom(1L)
//                .dimensionA(BigDecimal.valueOf(350))
//                .dimensionB(BigDecimal.valueOf(350))
//                .dimensionA1BottomB(BigDecimal.valueOf(50))
//                .dimensionA2BottomB(BigDecimal.valueOf(50))
//                .dimensionB1BottomA(BigDecimal.valueOf(50))
//                .dimensionB2BottomA(BigDecimal.valueOf(50))
//                .cornerA1B1(new PartCornerStraightData(BigDecimal.valueOf(30), BigDecimal.valueOf(30)))
//                .cornerA1B2(new PartCornerStraightData(BigDecimal.valueOf(50), BigDecimal.valueOf(50)))
//                .cornerA2B1(new PartCornerRoundedData(BigDecimal.valueOf(30)))
//                .cornerA2B2(new PartCornerRoundedData(BigDecimal.valueOf(50)))
//                .build(), boards, edges, manufactureProperties);
//
//        ApplicationException applicationException = assertThrows(ApplicationException.class,
//                () -> partUtil.validate(PartDuplicatedFrameData.builder()
//                        .boardIdTop(1L)
//                        .boardIdA1Bottom(1L)
//                        .dimensionA(BigDecimal.valueOf(350))
//                        .dimensionB(BigDecimal.valueOf(350))
//                        .dimensionA1BottomB(BigDecimal.valueOf(50))
//                        .build(), boards, edges, manufactureProperties)
//        );
//        assertEquals("Invalid dimensions [2,011,1,511] maximum is ManufactureDimensionsData[x=2000, y=1500]", applicationException.getMessage());
//        assertEquals(WiwaException.ORDER_ITEM_PART_DIMENSION.name(), applicationException.getCode());
//
//        applicationException = assertThrows(ApplicationException.class,
//                () -> partUtil.validate(PartDuplicatedBasicData.builder()
//                        .boardIdTop(1L)
//                        .boardIdBottom(2L)
//                        .dimensionA(BigDecimal.valueOf(49))
//                        .dimensionB(BigDecimal.valueOf(350))
//                        .build(), boards, edges, manufactureProperties)
//        );
//        assertEquals("Invalid dimensions [49,350] minimum is ManufactureDimensionsData[x=50, y=50]", applicationException.getMessage());
//        assertEquals(WiwaException.ORDER_ITEM_PART_DIMENSION.name(), applicationException.getCode());
//    }
}
