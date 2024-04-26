package sk.janobono.wiwa.business.impl.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.janobono.wiwa.business.model.order.part.PartBasicData;
import sk.janobono.wiwa.business.model.order.part.PartCornerRoundedData;
import sk.janobono.wiwa.business.model.order.part.PartCornerStraightData;
import sk.janobono.wiwa.business.model.order.part.PartData;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class DataUtilTest {

    private DataUtil dataUtil;

    @BeforeEach
    void setUp() {
        final ObjectMapper objectMapper = new ObjectMapper();
        dataUtil = new DataUtil(objectMapper);
    }

    @Test
    void partChangeDataTest() {
        final PartData basic = PartBasicData.builder()
                .boardId(1L)
                .edgeIdA1(2L)
                .dimensionA(new BigDecimal("200.3"))
                .dimensionB(new BigDecimal("23.4"))
                .cornerA1B1(new PartCornerStraightData(BigDecimal.ONE, BigDecimal.ONE))
                .cornerA1B2(new PartCornerRoundedData(BigDecimal.ONE))
                .build();
        final String basicValue = dataUtil.serializeValue(basic);
        assertThat(basic).usingRecursiveComparison().isEqualTo(dataUtil.parseValue(basicValue, PartData.class));
    }

}
