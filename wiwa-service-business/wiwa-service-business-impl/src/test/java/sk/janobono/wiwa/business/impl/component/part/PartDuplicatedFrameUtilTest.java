package sk.janobono.wiwa.business.impl.component.part;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.janobono.wiwa.business.impl.model.summary.EdgeLengthData;
import sk.janobono.wiwa.business.model.DimensionsData;
import sk.janobono.wiwa.business.model.application.ManufacturePropertiesData;
import sk.janobono.wiwa.business.model.order.OrderBoardData;
import sk.janobono.wiwa.business.model.order.OrderEdgeData;
import sk.janobono.wiwa.business.model.order.part.PartCornerData;
import sk.janobono.wiwa.business.model.order.part.PartCornerRoundedData;
import sk.janobono.wiwa.business.model.order.part.PartDuplicatedFrameData;
import sk.janobono.wiwa.exception.ApplicationException;
import sk.janobono.wiwa.exception.WiwaException;
import sk.janobono.wiwa.model.BoardPosition;
import sk.janobono.wiwa.model.CornerPosition;
import sk.janobono.wiwa.model.EdgePosition;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PartDuplicatedFrameUtilTest {

    private ObjectMapper objectMapper;

    private Map<Long, OrderBoardData> boards;
    private Map<Long, OrderEdgeData> edges;
    private Map<Long, BigDecimal> thicknessMap;

    private ManufacturePropertiesData manufactureProperties;

    private PartDuplicatedFrameUtil partDuplicatedFrameUtil;

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
        thicknessMap = Map.of(
                1L, BigDecimal.valueOf(10),
                2L, BigDecimal.valueOf(20),
                3L, BigDecimal.valueOf(30)
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

        partDuplicatedFrameUtil = new PartDuplicatedFrameUtil();
    }


    @Test
    void boards_whenVariousBoards_thenTheseResults() {
        final Map<BoardPosition, Long> boards = PartDuplicatedFrameData.builder()
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
        assertThat(boards).hasSize(5);
    }

    @Test
    void edges_whenVariousBoards_thenTheseResults() {
        final Map<EdgePosition, Long> edges = PartDuplicatedFrameData.builder()
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
        assertThat(edges).hasSize(8);
    }

    @Test
    void corners_whenVariousBoards_thenTheseResults() {
        final Map<CornerPosition, PartCornerData> corners = PartDuplicatedFrameData.builder()
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
        assertThat(corners).hasSize(4);
    }

    @Test
    void validate_whenValidData_thenTheseResults() throws IOException {
        final PartDuplicatedFrameData part = objectMapper.readValue(getClass().getResource("/part_duplicated_frame.json"), PartDuplicatedFrameData.class);
        partDuplicatedFrameUtil.validate(part, boards, edges, manufactureProperties);
    }

    @Test
    void validate_whenValidData_thenParamsError() throws IOException {
        final PartDuplicatedFrameData[] parts = objectMapper.readValue(getClass().getResource("/part_duplicated_frame_parameters.json"), PartDuplicatedFrameData[].class);
        for (final PartDuplicatedFrameData part : parts) {
            final ApplicationException applicationException = assertThrows(ApplicationException.class,
                    () -> partDuplicatedFrameUtil.validate(part, boards, edges, manufactureProperties)
            );
            assertThat(applicationException.getCode()).isEqualTo(WiwaException.ORDER_ITEM_PROPERTIES.name());
        }
    }

    @Test
    void validate_whenValidData_thenPartBoardError() throws IOException {
        final PartDuplicatedFrameData[] parts = objectMapper.readValue(getClass().getResource("/part_duplicated_frame_board.json"), PartDuplicatedFrameData[].class);
        for (final PartDuplicatedFrameData part : parts) {
            final ApplicationException applicationException = assertThrows(ApplicationException.class,
                    () -> partDuplicatedFrameUtil.validate(part, boards, edges, manufactureProperties)
            );
            assertThat(applicationException.getCode()).isEqualTo(WiwaException.ORDER_ITEM_PART_BOARD.name());
        }
    }

    @Test
    void validate_whenValidData_thenPartEdgeError() throws IOException {
        final PartDuplicatedFrameData[] parts = objectMapper.readValue(getClass().getResource("/part_duplicated_frame_edge.json"), PartDuplicatedFrameData[].class);
        for (final PartDuplicatedFrameData part : parts) {
            final ApplicationException applicationException = assertThrows(ApplicationException.class,
                    () -> partDuplicatedFrameUtil.validate(part, boards, edges, manufactureProperties)
            );
            assertThat(applicationException.getCode()).isEqualTo(WiwaException.ORDER_ITEM_PART_EDGE.name());
        }
    }

    @Test
    void calculateBoardArea_whenPartDuplicatedFrame_thenTheseResults() throws IOException {
        final PartDuplicatedFrameData part = objectMapper.readValue(getClass().getResource("/part_duplicated_frame.json"), PartDuplicatedFrameData.class);
        final Map<BoardPosition, BigDecimal> areaMap = partDuplicatedFrameUtil.calculateBoardArea(part, manufactureProperties);

        assertThat(areaMap).hasSize(5);
        assertThat(areaMap.containsKey(BoardPosition.TOP)).isTrue();
        assertThat(areaMap.get(BoardPosition.TOP)).isEqualTo(new BigDecimal("0.270"));
        assertThat(areaMap.containsKey(BoardPosition.A1)).isTrue();
        assertThat(areaMap.get(BoardPosition.A1)).isEqualTo(new BigDecimal("0.057"));
        assertThat(areaMap.containsKey(BoardPosition.A2)).isTrue();
        assertThat(areaMap.get(BoardPosition.A2)).isEqualTo(new BigDecimal("0.057"));
        assertThat(areaMap.containsKey(BoardPosition.B1)).isTrue();
        assertThat(areaMap.get(BoardPosition.B1)).isEqualTo(new BigDecimal("0.033"));
        assertThat(areaMap.containsKey(BoardPosition.B2)).isTrue();
        assertThat(areaMap.get(BoardPosition.B2)).isEqualTo(new BigDecimal("0.033"));
    }

    @Test
    void calculateBoardArea_whenPartDuplicatedFrameVertical_thenTheseResults() throws IOException {
        final PartDuplicatedFrameData part = objectMapper.readValue(getClass().getResource("/part_duplicated_frame_vertical.json"), PartDuplicatedFrameData.class);
        final Map<BoardPosition, BigDecimal> areaMap = partDuplicatedFrameUtil.calculateBoardArea(part, manufactureProperties);

        assertThat(areaMap).hasSize(5);
        assertThat(areaMap.containsKey(BoardPosition.TOP)).isTrue();
        assertThat(areaMap.get(BoardPosition.TOP)).isEqualTo(new BigDecimal("0.270"));
        assertThat(areaMap.containsKey(BoardPosition.A1)).isTrue();
        assertThat(areaMap.get(BoardPosition.A1)).isEqualTo(new BigDecimal("0.033"));
        assertThat(areaMap.containsKey(BoardPosition.A2)).isTrue();
        assertThat(areaMap.get(BoardPosition.A2)).isEqualTo(new BigDecimal("0.033"));
        assertThat(areaMap.containsKey(BoardPosition.B1)).isTrue();
        assertThat(areaMap.get(BoardPosition.B1)).isEqualTo(new BigDecimal("0.057"));
        assertThat(areaMap.containsKey(BoardPosition.B2)).isTrue();
        assertThat(areaMap.get(BoardPosition.B2)).isEqualTo(new BigDecimal("0.057"));
    }

    @Test
    void calculateCutLength_whenPartDuplicatedFrame_thenTheseResults() throws IOException {
        final PartDuplicatedFrameData part = objectMapper.readValue(getClass().getResource("/part_duplicated_frame_cut.json"), PartDuplicatedFrameData.class);

        final Map<BigDecimal, BigDecimal> cutLengthMap = partDuplicatedFrameUtil.calculateCutLength(part, thicknessMap, manufactureProperties);

        assertThat(cutLengthMap).hasSize(3);
        assertThat(cutLengthMap.containsKey(BigDecimal.valueOf(10))).isTrue();
        assertThat(cutLengthMap.get(BigDecimal.valueOf(10))).isEqualTo(new BigDecimal("2.080"));
        assertThat(cutLengthMap.containsKey(BigDecimal.valueOf(20))).isTrue();
        assertThat(cutLengthMap.get(BigDecimal.valueOf(20))).isEqualTo(new BigDecimal("4.160"));
        assertThat(cutLengthMap.containsKey(BigDecimal.valueOf(30))).isTrue();
        assertThat(cutLengthMap.get(BigDecimal.valueOf(30))).isEqualTo(new BigDecimal("2.300"));
    }

    @Test
    void calculateEdgeLength_whenPartDuplicatedFrame_thenTheseResults() throws IOException {
        final PartDuplicatedFrameData part = objectMapper.readValue(getClass().getResource("/part_duplicated_frame.json"), PartDuplicatedFrameData.class);
        final Map<Long, EdgeLengthData> edgeLengthMap = partDuplicatedFrameUtil.calculateEdgeLength(part, manufactureProperties);

        assertThat(edgeLengthMap).hasSize(2);
        assertThat(edgeLengthMap.containsKey(1L)).isTrue();
        assertThat(edgeLengthMap.get(1L)).isEqualTo(new EdgeLengthData(new BigDecimal("1.600"), new BigDecimal("1.760")));

        assertThat(edgeLengthMap.containsKey(2L)).isTrue();
        assertThat(edgeLengthMap.get(2L)).isEqualTo(new EdgeLengthData(new BigDecimal("2.000"), new BigDecimal("2.160")));
    }
}
