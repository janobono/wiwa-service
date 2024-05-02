package sk.janobono.wiwa.business.impl.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.janobono.wiwa.business.model.DimensionsData;
import sk.janobono.wiwa.business.model.application.ManufacturePropertiesData;
import sk.janobono.wiwa.business.model.order.OrderBoardData;
import sk.janobono.wiwa.business.model.order.OrderEdgeData;
import sk.janobono.wiwa.business.model.order.part.PartData;
import sk.janobono.wiwa.business.model.order.summary.OrderItemSummaryData;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;

class SummaryUtilTest {

    private ObjectMapper objectMapper;

    private Map<Long, OrderBoardData> boards;
    private Map<Long, OrderEdgeData> edges;
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
                        .build(),
                2L, OrderBoardData.builder()
                        .id(2L)
                        .orientation(false)
                        .weight(new BigDecimal("17.2"))
                        .length(BigDecimal.valueOf(2000))
                        .width(BigDecimal.valueOf(1500))
                        .thickness(BigDecimal.valueOf(20))
                        .price(BigDecimal.valueOf(110))
                        .build(),
                3L, OrderBoardData.builder()
                        .id(3L)
                        .orientation(false)
                        .weight(new BigDecimal("19.2"))
                        .length(BigDecimal.valueOf(2000))
                        .width(BigDecimal.valueOf(1500))
                        .thickness(BigDecimal.valueOf(30))
                        .price(BigDecimal.valueOf(120))
                        .build()
        );

        edges = Map.of(
                1L, OrderEdgeData.builder()
                        .weight(new BigDecimal("0.1"))
                        .width(BigDecimal.valueOf(18))
                        .price(BigDecimal.valueOf(100))
                        .build(),
                2L, OrderEdgeData.builder()
                        .weight(new BigDecimal("0.15"))
                        .width(BigDecimal.valueOf(28))
                        .price(BigDecimal.valueOf(110))
                        .build(),
                3L, OrderEdgeData.builder()
                        .weight(new BigDecimal("0.19"))
                        .width(BigDecimal.valueOf(38))
                        .price(BigDecimal.valueOf(120))
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
                new PriceUtil());
    }

    @Test
    void countItemSummary_whenPartBasic_thenTheseResults() throws IOException {
        final PartData part = objectMapper.readValue(getClass().getResource("/part_basic.json"), PartData.class);
        final OrderItemSummaryData orderItemSummary = summaryUtil.calculateItemSummary(part,
                1,
                boards.values().stream().collect(Collectors.toMap(OrderBoardData::id, OrderBoardData::thickness)),
                manufactureProperties);

        System.out.println(orderItemSummary);
        // TODO

    }

    @Test
    void countItemSummary_whenPartDuplicatedBasic_thenTheseResults() throws IOException {
        final PartData part = objectMapper.readValue(getClass().getResource("/part_duplicated.json"), PartData.class);
        // TODO

    }

    @Test
    void countItemSummary_whenPartFrame_thenTheseResults() throws IOException {
        final PartData part = objectMapper.readValue(getClass().getResource("/part_frame.json"), PartData.class);
        // TODO

    }

    @Test
    void countItemSummary_whenPartDuplicatedFrame_thenTheseResults() throws IOException {
        final PartData part = objectMapper.readValue(getClass().getResource("/part_duplicated_frame.json"), PartData.class);
        // TODO

    }

}
