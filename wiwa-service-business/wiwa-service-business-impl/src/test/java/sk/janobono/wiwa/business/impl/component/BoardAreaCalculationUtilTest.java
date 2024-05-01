package sk.janobono.wiwa.business.impl.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.janobono.wiwa.business.model.DimensionsData;
import sk.janobono.wiwa.business.model.application.ManufacturePropertiesData;
import sk.janobono.wiwa.business.model.order.part.BoardPosition;
import sk.janobono.wiwa.business.model.order.part.PartData;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class BoardAreaCalculationUtilTest {

    private ObjectMapper objectMapper;

    private ManufacturePropertiesData manufactureProperties;

    private BoardAreaCalculationUtil boardAreaCalculationUtil;

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

        boardAreaCalculationUtil = new BoardAreaCalculationUtil();
    }

    @Test
    void calculateArea_whenPartBasic_thenTheseResults() throws IOException {
        final PartData part = objectMapper.readValue(getClass().getResource("/part_basic.json"), PartData.class);

        final Map<BoardPosition, BigDecimal> areaMap = boardAreaCalculationUtil.calculateArea(part, manufactureProperties);

        assertThat(areaMap.size()).isEqualTo(1);
        assertThat(areaMap.containsKey(BoardPosition.TOP)).isTrue();
        assertThat(areaMap.get(BoardPosition.TOP)).isEqualTo(new BigDecimal("0.250"));
    }

    @Test
    void calculateArea_whenPartDuplicatedBasic_thenTheseResults() throws IOException {
        final PartData part = objectMapper.readValue(getClass().getResource("/part_duplicated.json"), PartData.class);

        final Map<BoardPosition, BigDecimal> areaMap = boardAreaCalculationUtil.calculateArea(part, manufactureProperties);

        assertThat(areaMap.size()).isEqualTo(2);
        assertThat(areaMap.containsKey(BoardPosition.TOP)).isTrue();
        assertThat(areaMap.get(BoardPosition.TOP)).isEqualTo(new BigDecimal("0.270"));
        assertThat(areaMap.containsKey(BoardPosition.BOTTOM)).isTrue();
        assertThat(areaMap.get(BoardPosition.BOTTOM)).isEqualTo(new BigDecimal("0.270"));
    }

    @Test
    void calculateArea_whenPartFrame_thenTheseResults() throws IOException {
        final PartData part = objectMapper.readValue(getClass().getResource("/part_frame.json"), PartData.class);
        final Map<BoardPosition, BigDecimal> areaMap = boardAreaCalculationUtil.calculateArea(part, manufactureProperties);

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
    void calculateArea_whenPartDuplicatedFrame_thenTheseResults() throws IOException {
        final PartData part = objectMapper.readValue(getClass().getResource("/part_duplicated_frame.json"), PartData.class);
        final Map<BoardPosition, BigDecimal> areaMap = boardAreaCalculationUtil.calculateArea(part, manufactureProperties);

        assertThat(areaMap.size()).isEqualTo(5);
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
    void calculateArea_whenPartDuplicatedFrameVertical_thenTheseResults() throws IOException {
        final PartData part = objectMapper.readValue(getClass().getResource("/part_duplicated_frame_vertical.json"), PartData.class);
        final Map<BoardPosition, BigDecimal> areaMap = boardAreaCalculationUtil.calculateArea(part, manufactureProperties);

        assertThat(areaMap.size()).isEqualTo(5);
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
}
