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
import sk.janobono.wiwa.business.model.order.part.PartBasicData;
import sk.janobono.wiwa.business.model.order.part.PartCornerData;
import sk.janobono.wiwa.business.model.order.part.PartCornerRoundedData;
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

class PartBasicUtilTest {

    private ObjectMapper objectMapper;

    private Map<Long, OrderBoardData> boards;
    private Map<Long, OrderEdgeData> edges;
    private Map<Long, BigDecimal> thicknessMap;

    private ManufacturePropertiesData manufactureProperties;

    private PartBasicUtil partBasicUtil;

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

        partBasicUtil = new PartBasicUtil();
    }

    @Test
    void boards_whenVariousBoards_thenTheseResults() {
        final Map<BoardPosition, Long> boards = PartBasicData.builder()
                .boardId(1L)
                .build()
                .boards();
        assertThat(boards.get(BoardPosition.TOP)).isEqualTo(1L);
        assertThat(boards).hasSize(1);
    }

    @Test
    void edges_whenVariousBoards_thenTheseResults() {
        final Map<EdgePosition, Long> edges = PartBasicData.builder()
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
        assertThat(edges).hasSize(4);
    }

    @Test
    void corners_whenVariousBoards_thenTheseResults() {
        final Map<CornerPosition, PartCornerData> corners = PartBasicData.builder()
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
        final PartBasicData part = objectMapper.readValue(getClass().getResource("/part_basic.json"), PartBasicData.class);
        partBasicUtil.validate(part, boards, edges, manufactureProperties);
    }

    @Test
    void validate_whenValidData_thenParamsError() throws IOException {
        final PartBasicData[] parts = objectMapper.readValue(getClass().getResource("/part_basic_parameters.json"), PartBasicData[].class);
        for (final PartBasicData part : parts) {
            final ApplicationException applicationException = assertThrows(ApplicationException.class,
                    () -> partBasicUtil.validate(part, boards, edges, manufactureProperties)
            );
            assertThat(applicationException.getCode()).isEqualTo(WiwaException.ORDER_ITEM_PROPERTIES.name());
        }
    }

    @Test
    void validate_whenValidData_thenPartBoardError() throws IOException {
        final PartBasicData[] parts = objectMapper.readValue(getClass().getResource("/part_basic_board.json"), PartBasicData[].class);
        for (final PartBasicData part : parts) {
            final ApplicationException applicationException = assertThrows(ApplicationException.class,
                    () -> partBasicUtil.validate(part, boards, edges, manufactureProperties)
            );
            assertThat(applicationException.getCode()).isEqualTo(WiwaException.ORDER_ITEM_PART_BOARD.name());
        }
    }

    @Test
    void validate_whenValidData_thenPartEdgeError() throws IOException {
        final PartBasicData[] parts = objectMapper.readValue(getClass().getResource("/part_basic_edge.json"), PartBasicData[].class);
        for (final PartBasicData part : parts) {
            final ApplicationException applicationException = assertThrows(ApplicationException.class,
                    () -> partBasicUtil.validate(part, boards, edges, manufactureProperties)
            );
            assertThat(applicationException.getCode()).isEqualTo(WiwaException.ORDER_ITEM_PART_EDGE.name());
        }
    }

    @Test
    void validate_whenValidData_thenPartCornerError() throws IOException {
        final PartBasicData[] parts = objectMapper.readValue(getClass().getResource("/part_basic_corner.json"), PartBasicData[].class);
        for (final PartBasicData part : parts) {
            final ApplicationException applicationException = assertThrows(ApplicationException.class,
                    () -> partBasicUtil.validate(part, boards, edges, manufactureProperties)
            );
            assertThat(applicationException.getCode()).isEqualTo(WiwaException.ORDER_ITEM_PART_CORNER.name());
        }
    }

    @Test
    void calculateBoardArea_whenPartBasic_thenTheseResults() throws IOException {
        final PartBasicData part = objectMapper.readValue(getClass().getResource("/part_basic.json"), PartBasicData.class);

        final Map<BoardPosition, BigDecimal> areaMap = partBasicUtil.calculateBoardArea(part, manufactureProperties);

        assertThat(areaMap).hasSize(1);
        assertThat(areaMap.containsKey(BoardPosition.TOP)).isTrue();
        assertThat(areaMap.get(BoardPosition.TOP)).isEqualTo(new BigDecimal("0.250"));
    }

    @Test
    void calculateCutLength_whenPartBasic_thenTheseResults() throws IOException {
        final PartBasicData part = objectMapper.readValue(getClass().getResource("/part_basic.json"), PartBasicData.class);

        final Map<BigDecimal, BigDecimal> cutLengthMap = partBasicUtil.calculateCutLength(part, thicknessMap, manufactureProperties);

        assertThat(cutLengthMap).hasSize(1);
        assertThat(cutLengthMap.containsKey(BigDecimal.valueOf(10))).isTrue();
        assertThat(cutLengthMap.get(BigDecimal.valueOf(10))).isEqualTo(new BigDecimal("2.300"));
    }

    @Test
    void calculateEdgeLength_whenPartBasic_thenTheseResults() throws IOException {
        final PartBasicData part = objectMapper.readValue(getClass().getResource("/part_basic_edge_length_calculation.json"), PartBasicData.class);

        final Map<Long, EdgeLengthData> edgeLengthMap = partBasicUtil.calculateEdgeLength(part, manufactureProperties);

        assertThat(edgeLengthMap).hasSize(5);
        assertThat(edgeLengthMap.containsKey(1L)).isTrue();
        assertThat(edgeLengthMap.get(1L)).isEqualTo(new EdgeLengthData(new BigDecimal("2.000"), new BigDecimal("2.160")));
        assertThat(edgeLengthMap.containsKey(2L)).isTrue();
        assertThat(edgeLengthMap.get(2L)).isEqualTo(new EdgeLengthData(new BigDecimal("0.071"), new BigDecimal("0.111")));
        assertThat(edgeLengthMap.containsKey(3L)).isTrue();
        assertThat(edgeLengthMap.get(3L)).isEqualTo(new EdgeLengthData(new BigDecimal("0.071"), new BigDecimal("0.111")));
        assertThat(edgeLengthMap.containsKey(4L)).isTrue();
        assertThat(edgeLengthMap.get(4L)).isEqualTo(new EdgeLengthData(new BigDecimal("0.079"), new BigDecimal("0.119")));
        assertThat(edgeLengthMap.containsKey(5L)).isTrue();
        assertThat(edgeLengthMap.get(5L)).isEqualTo(new EdgeLengthData(new BigDecimal("0.079"), new BigDecimal("0.119")));
    }
}
