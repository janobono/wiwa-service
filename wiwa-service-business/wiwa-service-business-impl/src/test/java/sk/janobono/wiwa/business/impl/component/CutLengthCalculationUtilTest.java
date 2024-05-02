package sk.janobono.wiwa.business.impl.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.janobono.wiwa.business.model.DimensionsData;
import sk.janobono.wiwa.business.model.application.ManufacturePropertiesData;
import sk.janobono.wiwa.business.model.order.part.PartData;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class CutLengthCalculationUtilTest {

    private ObjectMapper objectMapper;

    private Map<Long, BigDecimal> thicknessMap;

    private ManufacturePropertiesData manufactureProperties;

    private CutLengthCalculationUtil cutLengthCalculationUtil;

    @BeforeEach
    void setUp() {
        objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();

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

        cutLengthCalculationUtil = new CutLengthCalculationUtil();
    }

    @Test
    void calculateBoardCutLength_whenPartBasic_thenTheseResults() throws IOException {
        final PartData part = objectMapper.readValue(getClass().getResource("/part_basic.json"), PartData.class);

        final Map<BigDecimal, BigDecimal> cutLengthMap = cutLengthCalculationUtil.calculateBoardCutLength(part, thicknessMap, manufactureProperties);

        assertThat(cutLengthMap.size()).isEqualTo(1);
        assertThat(cutLengthMap.containsKey(BigDecimal.valueOf(10))).isTrue();
        assertThat(cutLengthMap.get(BigDecimal.valueOf(10))).isEqualTo(new BigDecimal("2.300"));
    }

    @Test
    void calculateBoardCutLength_whenPartDuplicatedBasic_thenTheseResults() throws IOException {
        final PartData part = objectMapper.readValue(getClass().getResource("/part_duplicated.json"), PartData.class);

        final Map<BigDecimal, BigDecimal> cutLengthMap = cutLengthCalculationUtil.calculateBoardCutLength(part, thicknessMap, manufactureProperties);

        assertThat(cutLengthMap.size()).isEqualTo(3);
        assertThat(cutLengthMap.containsKey(BigDecimal.valueOf(10))).isTrue();
        assertThat(cutLengthMap.get(BigDecimal.valueOf(10))).isEqualTo(new BigDecimal("2.080"));
        assertThat(cutLengthMap.containsKey(BigDecimal.valueOf(20))).isTrue();
        assertThat(cutLengthMap.get(BigDecimal.valueOf(20))).isEqualTo(new BigDecimal("2.080"));
        assertThat(cutLengthMap.containsKey(BigDecimal.valueOf(30))).isTrue();
        assertThat(cutLengthMap.get(BigDecimal.valueOf(30))).isEqualTo(new BigDecimal("2.300"));
    }

    @Test
    void calculateBoardCutLength_whenPartFrame_thenTheseResults() throws IOException {
        final PartData part = objectMapper.readValue(getClass().getResource("/part_frame.json"), PartData.class);

        final Map<BigDecimal, BigDecimal> cutLengthMap = cutLengthCalculationUtil.calculateBoardCutLength(part, thicknessMap, manufactureProperties);

        assertThat(cutLengthMap.size()).isEqualTo(1);
        assertThat(cutLengthMap.containsKey(BigDecimal.valueOf(10))).isTrue();
        assertThat(cutLengthMap.get(BigDecimal.valueOf(10))).isEqualTo(new BigDecimal("4.000"));
    }

    @Test
    void calculateBoardCutLength_whenPartDuplicatedFrame_thenTheseResults() throws IOException {
        final PartData part = objectMapper.readValue(getClass().getResource("/part_duplicated_frame_cut.json"), PartData.class);

        final Map<BigDecimal, BigDecimal> cutLengthMap = cutLengthCalculationUtil.calculateBoardCutLength(part, thicknessMap, manufactureProperties);

        assertThat(cutLengthMap.size()).isEqualTo(3);
        assertThat(cutLengthMap.containsKey(BigDecimal.valueOf(10))).isTrue();
        assertThat(cutLengthMap.get(BigDecimal.valueOf(10))).isEqualTo(new BigDecimal("2.080"));
        assertThat(cutLengthMap.containsKey(BigDecimal.valueOf(20))).isTrue();
        assertThat(cutLengthMap.get(BigDecimal.valueOf(20))).isEqualTo(new BigDecimal("4.160"));
        assertThat(cutLengthMap.containsKey(BigDecimal.valueOf(30))).isTrue();
        assertThat(cutLengthMap.get(BigDecimal.valueOf(30))).isEqualTo(new BigDecimal("2.300"));
    }
}
