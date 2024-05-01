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
        Map<CornerPosition, PartCornerData> corners = PartBasicData.builder()
                .cornerA1B1(new PartCornerRoundedData(1L, BigDecimal.ZERO))
                .cornerA1B2(new PartCornerRoundedData(1L, BigDecimal.ONE))
                .cornerA2B1(new PartCornerRoundedData(1L, BigDecimal.TWO))
                .cornerA2B2(new PartCornerRoundedData(1L, BigDecimal.TEN))
                .build()
                .corners();
        assertThat(corners.get(CornerPosition.A1B1).dimensions()).isEqualTo(new DimensionsData(BigDecimal.ZERO, BigDecimal.ZERO));
        assertThat(corners.get(CornerPosition.A1B2).dimensions()).isEqualTo(new DimensionsData(BigDecimal.ONE, BigDecimal.ONE));
        assertThat(corners.get(CornerPosition.A2B1).dimensions()).isEqualTo(new DimensionsData(BigDecimal.TWO, BigDecimal.TWO));
        assertThat(corners.get(CornerPosition.A2B2).dimensions()).isEqualTo(new DimensionsData(BigDecimal.TEN, BigDecimal.TEN));
        assertThat(corners.size()).isEqualTo(4);

        corners = PartFrameData.builder()
                .build()
                .corners();
        assertThat(corners.isEmpty()).isTrue();

        corners = PartDuplicatedBasicData.builder()
                .cornerA1B1(new PartCornerRoundedData(1L, BigDecimal.ZERO))
                .cornerA1B2(new PartCornerRoundedData(1L, BigDecimal.ONE))
                .cornerA2B1(new PartCornerRoundedData(1L, BigDecimal.TWO))
                .cornerA2B2(new PartCornerRoundedData(1L, BigDecimal.TEN))
                .build()
                .corners();
        assertThat(corners.get(CornerPosition.A1B1).dimensions()).isEqualTo(new DimensionsData(BigDecimal.ZERO, BigDecimal.ZERO));
        assertThat(corners.get(CornerPosition.A1B2).dimensions()).isEqualTo(new DimensionsData(BigDecimal.ONE, BigDecimal.ONE));
        assertThat(corners.get(CornerPosition.A2B1).dimensions()).isEqualTo(new DimensionsData(BigDecimal.TWO, BigDecimal.TWO));
        assertThat(corners.get(CornerPosition.A2B2).dimensions()).isEqualTo(new DimensionsData(BigDecimal.TEN, BigDecimal.TEN));
        assertThat(corners.size()).isEqualTo(4);

        corners = PartDuplicatedFrameData.builder()
                .cornerA1B1(new PartCornerRoundedData(1L, BigDecimal.ZERO))
                .cornerA1B2(new PartCornerRoundedData(1L, BigDecimal.ONE))
                .cornerA2B1(new PartCornerRoundedData(1L, BigDecimal.TWO))
                .cornerA2B2(new PartCornerRoundedData(1L, BigDecimal.TEN))
                .build()
                .corners();
        assertThat(corners.get(CornerPosition.A1B1).dimensions()).isEqualTo(new DimensionsData(BigDecimal.ZERO, BigDecimal.ZERO));
        assertThat(corners.get(CornerPosition.A1B2).dimensions()).isEqualTo(new DimensionsData(BigDecimal.ONE, BigDecimal.ONE));
        assertThat(corners.get(CornerPosition.A2B1).dimensions()).isEqualTo(new DimensionsData(BigDecimal.TWO, BigDecimal.TWO));
        assertThat(corners.get(CornerPosition.A2B2).dimensions()).isEqualTo(new DimensionsData(BigDecimal.TEN, BigDecimal.TEN));
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

    @Test
    void validate_whenValidData_thenParamsError() throws IOException {
        final PartUtil partUtil = new PartUtil();

        PartData[] parts = objectMapper.readValue(getClass().getResource("/part_basic_parameters.json"), PartData[].class);
        for (final PartData part : parts) {
            final ApplicationException applicationException = assertThrows(ApplicationException.class,
                    () -> partUtil.validate(part, boards, edges, manufactureProperties)
            );
            assertThat(applicationException.getCode()).isEqualTo(WiwaException.ORDER_ITEM_PROPERTIES.name());
        }

        parts = objectMapper.readValue(getClass().getResource("/part_duplicated_parameters.json"), PartData[].class);
        for (final PartData part : parts) {
            final ApplicationException applicationException = assertThrows(ApplicationException.class,
                    () -> partUtil.validate(part, boards, edges, manufactureProperties)
            );
            assertThat(applicationException.getCode()).isEqualTo(WiwaException.ORDER_ITEM_PROPERTIES.name());
        }

        parts = objectMapper.readValue(getClass().getResource("/part_frame_parameters.json"), PartData[].class);
        for (final PartData part : parts) {
            final ApplicationException applicationException = assertThrows(ApplicationException.class,
                    () -> partUtil.validate(part, boards, edges, manufactureProperties)
            );
            assertThat(applicationException.getCode()).isEqualTo(WiwaException.ORDER_ITEM_PROPERTIES.name());
        }

        parts = objectMapper.readValue(getClass().getResource("/part_duplicated_frame_parameters.json"), PartData[].class);
        for (final PartData part : parts) {
            final ApplicationException applicationException = assertThrows(ApplicationException.class,
                    () -> partUtil.validate(part, boards, edges, manufactureProperties)
            );
            assertThat(applicationException.getCode()).isEqualTo(WiwaException.ORDER_ITEM_PROPERTIES.name());
        }
    }

    @Test
    void validate_whenValidData_thenPartBoardError() throws IOException {
        final PartUtil partUtil = new PartUtil();

        PartData[] parts = objectMapper.readValue(getClass().getResource("/part_basic_board.json"), PartData[].class);
        for (final PartData part : parts) {
            final ApplicationException applicationException = assertThrows(ApplicationException.class,
                    () -> partUtil.validate(part, boards, edges, manufactureProperties)
            );
            assertThat(applicationException.getCode()).isEqualTo(WiwaException.ORDER_ITEM_PART_BOARD.name());
        }

        parts = objectMapper.readValue(getClass().getResource("/part_duplicated_board.json"), PartData[].class);
        for (final PartData part : parts) {
            final ApplicationException applicationException = assertThrows(ApplicationException.class,
                    () -> partUtil.validate(part, boards, edges, manufactureProperties)
            );
            assertThat(applicationException.getCode()).isEqualTo(WiwaException.ORDER_ITEM_PART_BOARD.name());
        }

        parts = objectMapper.readValue(getClass().getResource("/part_frame_board.json"), PartData[].class);
        for (final PartData part : parts) {
            final ApplicationException applicationException = assertThrows(ApplicationException.class,
                    () -> partUtil.validate(part, boards, edges, manufactureProperties)
            );
            assertThat(applicationException.getCode()).isEqualTo(WiwaException.ORDER_ITEM_PART_BOARD.name());
        }

        parts = objectMapper.readValue(getClass().getResource("/part_duplicated_frame_board.json"), PartData[].class);
        for (final PartData part : parts) {
            final ApplicationException applicationException = assertThrows(ApplicationException.class,
                    () -> partUtil.validate(part, boards, edges, manufactureProperties)
            );
            assertThat(applicationException.getCode()).isEqualTo(WiwaException.ORDER_ITEM_PART_BOARD.name());
        }
    }

    @Test
    void validate_whenValidData_thenPartEdgeError() throws IOException {
        final PartUtil partUtil = new PartUtil();

        PartData[] parts = objectMapper.readValue(getClass().getResource("/part_basic_edge.json"), PartData[].class);
        for (final PartData part : parts) {
            final ApplicationException applicationException = assertThrows(ApplicationException.class,
                    () -> partUtil.validate(part, boards, edges, manufactureProperties)
            );
            assertThat(applicationException.getCode()).isEqualTo(WiwaException.ORDER_ITEM_PART_EDGE.name());
        }

        parts = objectMapper.readValue(getClass().getResource("/part_duplicated_edge.json"), PartData[].class);
        for (final PartData part : parts) {
            final ApplicationException applicationException = assertThrows(ApplicationException.class,
                    () -> partUtil.validate(part, boards, edges, manufactureProperties)
            );
            assertThat(applicationException.getCode()).isEqualTo(WiwaException.ORDER_ITEM_PART_EDGE.name());
        }

        parts = objectMapper.readValue(getClass().getResource("/part_frame_edge.json"), PartData[].class);
        for (final PartData part : parts) {
            final ApplicationException applicationException = assertThrows(ApplicationException.class,
                    () -> partUtil.validate(part, boards, edges, manufactureProperties)
            );
            assertThat(applicationException.getCode()).isEqualTo(WiwaException.ORDER_ITEM_PART_EDGE.name());
        }

        parts = objectMapper.readValue(getClass().getResource("/part_duplicated_frame_edge.json"), PartData[].class);
        for (final PartData part : parts) {
            final ApplicationException applicationException = assertThrows(ApplicationException.class,
                    () -> partUtil.validate(part, boards, edges, manufactureProperties)
            );
            assertThat(applicationException.getCode()).isEqualTo(WiwaException.ORDER_ITEM_PART_EDGE.name());
        }
    }

    @Test
    void validate_whenValidData_thenPartCornerError() throws IOException {
        final PartUtil partUtil = new PartUtil();

        final PartData[] parts = objectMapper.readValue(getClass().getResource("/part_basic_corner.json"), PartData[].class);
        for (final PartData part : parts) {
            final ApplicationException applicationException = assertThrows(ApplicationException.class,
                    () -> partUtil.validate(part, boards, edges, manufactureProperties)
            );
            assertThat(applicationException.getCode()).isEqualTo(WiwaException.ORDER_ITEM_PART_CORNER.name());
        }
    }
}
