package sk.janobono.wiwa.business.impl.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.janobono.wiwa.business.model.DimensionsData;
import sk.janobono.wiwa.business.model.order.part.*;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class DataUtilTest {

    private DataUtil dataUtil;

    @BeforeEach
    void setUp() {
        final ObjectMapper objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();
        dataUtil = new DataUtil(objectMapper);
    }

    @Test
    void partChangeDataTest() {
        PartData part = PartBasicData.builder()
                .rotate(true)
                .boardId(1L)
                .edgeIdA1(1L)
                .edgeIdA2(1L)
                .edgeIdB1(1L)
                .edgeIdB2(1L)
                .dimensionsTOP(new DimensionsData(BigDecimal.valueOf(250), BigDecimal.valueOf(400)))
                .cornerA1B1(new PartCornerStraightData(1L, new DimensionsData(BigDecimal.valueOf(50), BigDecimal.valueOf(30))))
                .cornerA1B2(new PartCornerStraightData(1L, new DimensionsData(BigDecimal.valueOf(30), BigDecimal.valueOf(50))))
                .cornerA2B1(new PartCornerRoundedData(1L, BigDecimal.valueOf(30)))
                .cornerA2B2(new PartCornerRoundedData(1L, BigDecimal.valueOf(50)))
                .build();
        String value = dataUtil.serializeValue(part);
        assertThat(part).usingRecursiveComparison().isEqualTo(dataUtil.parseValue(value, PartData.class));

        part = PartDuplicatedBasicData.builder()
                .rotate(true)
                .boardId(1L)
                .boardIdBottom(2L)
                .edgeIdA1(3L)
                .edgeIdA2(3L)
                .edgeIdB1(3L)
                .edgeIdB2(3L)
                .dimensionsTOP(new DimensionsData(BigDecimal.valueOf(345), BigDecimal.valueOf(450)))
                .cornerA1B1(new PartCornerStraightData(1L, new DimensionsData(BigDecimal.valueOf(60), BigDecimal.valueOf(40))))
                .cornerA1B2(new PartCornerStraightData(1L, new DimensionsData(BigDecimal.valueOf(40), BigDecimal.valueOf(60))))
                .cornerA2B1(new PartCornerRoundedData(1L, BigDecimal.valueOf(30)))
                .cornerA2B2(new PartCornerRoundedData(1L, BigDecimal.valueOf(50)))
                .build();
        value = dataUtil.serializeValue(part);
        assertThat(part).usingRecursiveComparison().isEqualTo(dataUtil.parseValue(value, PartData.class));

        part = PartFrameData.builder()
                .boardIdA1(1L)
                .boardIdA2(1L)
                .boardIdB1(1L)
                .boardIdB2(1L)
                .edgeIdA1(1L)
                .edgeIdA1I(1L)
                .edgeIdA2(1L)
                .edgeIdA2I(1L)
                .edgeIdB1(1L)
                .edgeIdB1I(1L)
                .edgeIdB2(1L)
                .edgeIdB2I(1L)
                .dimensionsTOP(new DimensionsData(BigDecimal.valueOf(400), BigDecimal.valueOf(400)))
                .dimensionsA1(new DimensionsData(BigDecimal.valueOf(80), BigDecimal.valueOf(80)))
                .dimensionsA2(new DimensionsData(BigDecimal.valueOf(90), BigDecimal.valueOf(90)))
                .dimensionsB1(new DimensionsData(BigDecimal.valueOf(100), BigDecimal.valueOf(100)))
                .dimensionsB2(new DimensionsData(BigDecimal.valueOf(110), BigDecimal.valueOf(110)))
                .build();
        value = dataUtil.serializeValue(part);
        assertThat(part).usingRecursiveComparison().isEqualTo(dataUtil.parseValue(value, PartData.class));

        part = PartDuplicatedFrameData.builder()
                .rotate(true)
                .boardId(1L)
                .boardIdA1(1L)
                .boardIdA2(1L)
                .boardIdB1(1L)
                .boardIdB2(1L)
                .edgeIdA1(2L)
                .edgeIdA1I(1L)
                .edgeIdA2(2L)
                .edgeIdA2I(1L)
                .edgeIdB1(2L)
                .edgeIdB1I(1L)
                .edgeIdB2(2L)
                .edgeIdB2I(1L)
                .dimensionsTOP(new DimensionsData(BigDecimal.valueOf(350), BigDecimal.valueOf(350)))
                .dimensionsA1(new DimensionsData(BigDecimal.valueOf(350), BigDecimal.valueOf(55)))
                .dimensionsA2(new DimensionsData(BigDecimal.valueOf(350), BigDecimal.valueOf(65)))
                .dimensionsB1(new DimensionsData(BigDecimal.valueOf(55), BigDecimal.valueOf(330)))
                .dimensionsB2(new DimensionsData(BigDecimal.valueOf(65), BigDecimal.valueOf(330)))
                .cornerA1B1(new PartCornerStraightData(1L, new DimensionsData(BigDecimal.valueOf(70), BigDecimal.valueOf(50))))
                .cornerA1B2(new PartCornerStraightData(1L, new DimensionsData(BigDecimal.valueOf(50), BigDecimal.valueOf(70))))
                .cornerA2B1(new PartCornerRoundedData(1L, BigDecimal.valueOf(30)))
                .cornerA2B2(new PartCornerRoundedData(1L, BigDecimal.valueOf(50)))
                .build();
        value = dataUtil.serializeValue(part);
        assertThat(part).usingRecursiveComparison().isEqualTo(dataUtil.parseValue(value, PartData.class));
    }
}
