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
import sk.janobono.wiwa.business.model.order.part.PartFrameData;
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

class PartFrameUtilTest {

    private ObjectMapper objectMapper;

    private Map<Long, OrderBoardData> boards;
    private Map<Long, OrderEdgeData> edges;
    private Map<Long, BigDecimal> thicknessMap;

    private ManufacturePropertiesData manufactureProperties;

    private PartFrameUtil partFrameUtil;

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

        partFrameUtil = new PartFrameUtil();
    }

    @Test
    void boards_whenVariousBoards_thenTheseResults() {
        final Map<BoardPosition, Long> boards = PartFrameData.builder()
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
    }

    @Test
    void edges_whenVariousBoards_thenTheseResults() {
        final Map<EdgePosition, Long> edges = PartFrameData.builder()
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
        final Map<CornerPosition, PartCornerData> corners = PartFrameData.builder()
                .build()
                .corners();
        assertThat(corners.isEmpty()).isTrue();
    }

    @Test
    void validate_whenValidData_thenTheseResults() throws IOException {
        final PartFrameData part = objectMapper.readValue(getClass().getResource("/part_frame.json"), PartFrameData.class);
        partFrameUtil.validate(part, boards, edges, manufactureProperties);
    }

    @Test
    void validate_whenValidData_thenParamsError() throws IOException {
        final PartFrameData[] parts = objectMapper.readValue(getClass().getResource("/part_frame_parameters.json"), PartFrameData[].class);
        for (final PartFrameData part : parts) {
            final ApplicationException applicationException = assertThrows(ApplicationException.class,
                    () -> partFrameUtil.validate(part, boards, edges, manufactureProperties)
            );
            assertThat(applicationException.getCode()).isEqualTo(WiwaException.ORDER_ITEM_PROPERTIES.name());
        }
    }

    @Test
    void validate_whenValidData_thenPartBoardError() throws IOException {
        final PartFrameData[] parts = objectMapper.readValue(getClass().getResource("/part_frame_board.json"), PartFrameData[].class);
        for (final PartFrameData part : parts) {
            final ApplicationException applicationException = assertThrows(ApplicationException.class,
                    () -> partFrameUtil.validate(part, boards, edges, manufactureProperties)
            );
            assertThat(applicationException.getCode()).isEqualTo(WiwaException.ORDER_ITEM_PART_BOARD.name());
        }
    }

    @Test
    void validate_whenValidData_thenPartEdgeError() throws IOException {
        final PartFrameData[] parts = objectMapper.readValue(getClass().getResource("/part_frame_edge.json"), PartFrameData[].class);
        for (final PartFrameData part : parts) {
            final ApplicationException applicationException = assertThrows(ApplicationException.class,
                    () -> partFrameUtil.validate(part, boards, edges, manufactureProperties)
            );
            assertThat(applicationException.getCode()).isEqualTo(WiwaException.ORDER_ITEM_PART_EDGE.name());
        }
    }

    @Test
    void calculateBoardArea_whenPartFrame_thenTheseResults() throws IOException {
        final PartFrameData part = objectMapper.readValue(getClass().getResource("/part_frame.json"), PartFrameData.class);
        final Map<BoardPosition, BigDecimal> areaMap = partFrameUtil.calculateBoardArea(part, manufactureProperties);

        assertThat(areaMap.size()).isEqualTo(4);
        assertThat(areaMap.containsKey(BoardPosition.A1)).isTrue();
        assertThat(areaMap.get(BoardPosition.A1)).isEqualTo(new BigDecimal("0.050"));
        assertThat(areaMap.containsKey(BoardPosition.A2)).isTrue();
        assertThat(areaMap.get(BoardPosition.A2)).isEqualTo(new BigDecimal("0.050"));
        assertThat(areaMap.containsKey(BoardPosition.B1)).isTrue();
        assertThat(areaMap.get(BoardPosition.B1)).isEqualTo(new BigDecimal("0.030"));
        assertThat(areaMap.containsKey(BoardPosition.B2)).isTrue();
        assertThat(areaMap.get(BoardPosition.B2)).isEqualTo(new BigDecimal("0.030"));
    }

    @Test
    void calculateCutLength_whenPartFrame_thenTheseResults() throws IOException {
        final PartFrameData part = objectMapper.readValue(getClass().getResource("/part_frame.json"), PartFrameData.class);

        final Map<BigDecimal, BigDecimal> cutLengthMap = partFrameUtil.calculateCutLength(part, thicknessMap, manufactureProperties);

        assertThat(cutLengthMap.size()).isEqualTo(1);
        assertThat(cutLengthMap.containsKey(BigDecimal.valueOf(10))).isTrue();
        assertThat(cutLengthMap.get(BigDecimal.valueOf(10))).isEqualTo(new BigDecimal("4.000"));
    }

    @Test
    void calculateEdgeLength_whenPartFrame_thenTheseResults() throws IOException {
        final PartFrameData part = objectMapper.readValue(getClass().getResource("/part_frame.json"), PartFrameData.class);
        final Map<Long, EdgeLengthData> edgeLengthMap = partFrameUtil.calculateEdgeLength(part, manufactureProperties);

        assertThat(edgeLengthMap.size()).isEqualTo(1);
        assertThat(edgeLengthMap.containsKey(1L)).isTrue();
        assertThat(edgeLengthMap.get(1L)).isEqualTo(new EdgeLengthData(new BigDecimal("3.600"), new BigDecimal("3.920")));
    }
}
