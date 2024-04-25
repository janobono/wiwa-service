package sk.janobono.wiwa.business.impl.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.janobono.wiwa.business.model.order.part.PartChangeBasicData;
import sk.janobono.wiwa.business.model.order.part.PartChangeData;
import sk.janobono.wiwa.business.model.order.part.PartCornerChangeRoundedData;
import sk.janobono.wiwa.business.model.order.part.PartCornerChangeStraightData;

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
        final PartChangeData basic = PartChangeBasicData.builder()
                .boardId(1L)
                .edgeIdA1(2L)
                .dimensionA(new BigDecimal("200.3"))
                .dimensionB(new BigDecimal("23.4"))
                .cornerA1B1(new PartCornerChangeStraightData(BigDecimal.ONE, BigDecimal.ONE))
                .cornerA1B2(new PartCornerChangeRoundedData(BigDecimal.ONE))
                .build();
        final String basicValue = dataUtil.serializeValue(basic);
        assertThat(basic).usingRecursiveComparison().isEqualTo(dataUtil.parseValue(basicValue, PartChangeData.class));
    }

}
