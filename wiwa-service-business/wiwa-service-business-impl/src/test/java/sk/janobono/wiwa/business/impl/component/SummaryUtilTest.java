package sk.janobono.wiwa.business.impl.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.janobono.wiwa.business.model.DimensionsData;
import sk.janobono.wiwa.business.model.application.ManufacturePropertiesData;
import sk.janobono.wiwa.business.model.order.OrderBoardData;
import sk.janobono.wiwa.business.model.order.part.PartData;
import sk.janobono.wiwa.business.model.order.summary.OrderItemSummaryData;
import sk.janobono.wiwa.dal.domain.OrderItemSummaryDo;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class SummaryUtilTest {

    private ObjectMapper objectMapper;

    private Map<Long, OrderBoardData> boards;
    private ManufacturePropertiesData manufactureProperties;

    private SummaryUtil summaryUtil;

    @BeforeEach
    void setUp() {
        objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();

        boards = Map.of(
                1L, OrderBoardData.builder()
                        .id(1L)
                        .orientation(false)
                        .weight(new BigDecimal("15.5"))
                        .length(BigDecimal.valueOf(2000))
                        .width(BigDecimal.valueOf(1500))
                        .thickness(BigDecimal.valueOf(10))
                        .price(BigDecimal.valueOf(100))
                        .build()
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

        summaryUtil = new SummaryUtil(
                new BoardAreaCalculationUtil(),
                new CutLengthCalculationUtil(),
                new EdgeLengthCalculationUtil(),
                new OrderSummaryCalculationUtil(new PriceUtil()),
                new OrderSummaryCodeMapper());
    }

    @Test
    void calculateItemSummary_whenPartBasic_thenTheseResults() throws IOException {
        final PartData part = objectMapper.readValue(getClass().getResource("/part_basic.json"), PartData.class);
        final OrderItemSummaryData orderItemSummary = summaryUtil.calculateItemSummary(part,
                1,
                boards.values().stream().collect(Collectors.toMap(OrderBoardData::id, OrderBoardData::thickness)),
                manufactureProperties);

        final List<OrderItemSummaryDo> items = summaryUtil.toOrderItemSummaries(1L, orderItemSummary);

        assertThat(orderItemSummary).usingRecursiveComparison().isEqualTo(summaryUtil.toOrderItemSummary(items));
    }
}
