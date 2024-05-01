package sk.janobono.wiwa.business.impl.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.janobono.wiwa.business.impl.model.summary.EdgeLengthData;
import sk.janobono.wiwa.business.model.DimensionsData;
import sk.janobono.wiwa.business.model.application.ManufacturePropertiesData;
import sk.janobono.wiwa.business.model.order.part.PartData;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class EdgeLengthCalculationUtilTest {

    private ObjectMapper objectMapper;

    private ManufacturePropertiesData manufactureProperties;

    private EdgeLengthCalculationUtil edgeLengthCalculationUtil;

    @BeforeEach
    void setUp() {
        objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();

        manufactureProperties = new ManufacturePropertiesData(
                new DimensionsData(BigDecimal.valueOf(50), BigDecimal.valueOf(50)),
                new DimensionsData(BigDecimal.valueOf(250), BigDecimal.valueOf(60)),
                new DimensionsData(BigDecimal.valueOf(250), BigDecimal.valueOf(70)),
                new DimensionsData(BigDecimal.valueOf(250), BigDecimal.valueOf(80)),
                BigDecimal.valueOf(8),
                BigDecimal.valueOf(40),
                BigDecimal.valueOf(10)
        );

        edgeLengthCalculationUtil = new EdgeLengthCalculationUtil();
    }

    @Test
    void calculateEdgeLength_whenPartBasic_thenTheseResults() throws IOException {
        final PartData part = objectMapper.readValue(getClass().getResource("/part_basic_edge_length_calculation.json"), PartData.class);

        final Map<Long, EdgeLengthData> edgeLengthMap = edgeLengthCalculationUtil.calculateEdgeLength(part, manufactureProperties);

        assertThat(edgeLengthMap.size()).isEqualTo(5);
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

    @Test
    void calculateEdgeLength_whenPartDuplicatedBasic_thenTheseResults() throws IOException {
        final PartData part = objectMapper.readValue(getClass().getResource("/part_duplicated.json"), PartData.class);

        final Map<Long, EdgeLengthData> edgeLengthMap = edgeLengthCalculationUtil.calculateEdgeLength(part, manufactureProperties);

        assertThat(edgeLengthMap.size()).isEqualTo(1);
        assertThat(edgeLengthMap.containsKey(3L)).isTrue();
        assertThat(edgeLengthMap.get(3L)).isEqualTo(new EdgeLengthData(new BigDecimal("2.000"), new BigDecimal("2.160")));
    }

    @Test
    void calculateEdgeLength_whenPartFrame_thenTheseResults() throws IOException {
        final PartData part = objectMapper.readValue(getClass().getResource("/part_frame.json"), PartData.class);
        final Map<Long, EdgeLengthData> edgeLengthMap = edgeLengthCalculationUtil.calculateEdgeLength(part, manufactureProperties);

        assertThat(edgeLengthMap.size()).isEqualTo(1);
        assertThat(edgeLengthMap.containsKey(1L)).isTrue();
        assertThat(edgeLengthMap.get(1L)).isEqualTo(new EdgeLengthData(new BigDecimal("3.600"), new BigDecimal("3.920")));
    }

    @Test
    void calculateEdgeLength_whenPartDuplicatedFrame_thenTheseResults() throws IOException {
        final PartData part = objectMapper.readValue(getClass().getResource("/part_duplicated_frame.json"), PartData.class);
        final Map<Long, EdgeLengthData> edgeLengthMap = edgeLengthCalculationUtil.calculateEdgeLength(part, manufactureProperties);

        assertThat(edgeLengthMap.size()).isEqualTo(2);
        assertThat(edgeLengthMap.containsKey(1L)).isTrue();
        assertThat(edgeLengthMap.get(1L)).isEqualTo(new EdgeLengthData(new BigDecimal("1.600"), new BigDecimal("1.760")));

        assertThat(edgeLengthMap.containsKey(2L)).isTrue();
        assertThat(edgeLengthMap.get(2L)).isEqualTo(new EdgeLengthData(new BigDecimal("2.000"), new BigDecimal("2.160")));
    }
}
