package sk.janobono.wiwa.business.impl.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.janobono.wiwa.business.model.DimensionsData;
import sk.janobono.wiwa.business.model.application.ManufacturePropertiesData;
import sk.janobono.wiwa.business.model.application.PriceForCuttingData;
import sk.janobono.wiwa.business.model.application.PriceForGluingEdgeData;
import sk.janobono.wiwa.business.model.application.PriceForGluingLayerData;
import sk.janobono.wiwa.business.model.order.OrderBoardData;
import sk.janobono.wiwa.business.model.order.OrderEdgeData;
import sk.janobono.wiwa.business.model.order.part.PartData;
import sk.janobono.wiwa.business.model.order.summary.*;
import sk.janobono.wiwa.dal.domain.OrderItemSummaryDo;
import sk.janobono.wiwa.dal.domain.OrderSummaryViewDo;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class SummaryUtilTest {

    private ObjectMapper objectMapper;

    private List<OrderBoardData> boards;
    private List<OrderEdgeData> edges;
    private ManufacturePropertiesData manufactureProperties;

    private SummaryUtil summaryUtil;

    @BeforeEach
    void setUp() {
        objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();

        boards = List.of(
                OrderBoardData.builder()
                        .id(1L)
                        .orientation(false)
                        .weight(BigDecimal.valueOf(1))
                        .length(BigDecimal.valueOf(2000))
                        .width(BigDecimal.valueOf(1500))
                        .thickness(BigDecimal.valueOf(10))
                        .price(BigDecimal.valueOf(100))
                        .build()
        );

        edges = List.of(
                OrderEdgeData.builder()
                        .id(1L)
                        .weight(BigDecimal.ONE)
                        .width(BigDecimal.valueOf(18))
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
                new OrderSummaryCalculationUtil(new PriceUtil()),
                new OrderSummaryCodeMapper());
    }

    @Test
    void calculateItemSummary_whenPartBasic_thenTheseResults() throws IOException {
        final PartData part = objectMapper.readValue(getClass().getResource("/part_basic.json"), PartData.class);
        final OrderItemSummaryData orderItemSummary = summaryUtil.calculateItemSummary(part,
                1,
                boards.stream().collect(Collectors.toMap(OrderBoardData::id, OrderBoardData::thickness)),
                manufactureProperties);

        final List<OrderItemSummaryDo> items = summaryUtil.toOrderItemSummaries(1L, orderItemSummary);

        assertThat(orderItemSummary).usingRecursiveComparison().isEqualTo(summaryUtil.toOrderItemSummary(items));
    }

    @Test
    void toOrderSummary_whenPartBasic_thenTheseResults() throws IOException {
        final PartData part = objectMapper.readValue(getClass().getResource("/part_basic.json"), PartData.class);
        final OrderItemSummaryData orderItemSummary = summaryUtil.calculateItemSummary(part,
                1,
                boards.stream().collect(Collectors.toMap(OrderBoardData::id, OrderBoardData::thickness)),
                manufactureProperties);

        final List<OrderItemSummaryDo> items = summaryUtil.toOrderItemSummaries(1L, orderItemSummary);

        final OrderSummaryData orderSummary = summaryUtil.toOrderSummary(
                boards,
                edges,
                BigDecimal.valueOf(20),
                List.of(
                        new PriceForCuttingData(BigDecimal.valueOf(10), BigDecimal.valueOf(1))
                ),
                new PriceForGluingLayerData(BigDecimal.valueOf(1)),
                List.of(
                        new PriceForGluingEdgeData(BigDecimal.valueOf(20), BigDecimal.valueOf(1))
                ),
                items.stream()
                        .map(item -> new OrderSummaryViewDo(1L, item.getCode(), item.getAmount()))
                        .toList()
        );

        assertThat(orderSummary.boardSummary().size()).isEqualTo(1);
        assertThat(orderSummary.boardSummary().getFirst()).usingRecursiveComparison().isEqualTo(new OrderBoardSummaryData(1L, new BigDecimal("0.250"), BigDecimal.valueOf(1), new BigDecimal("0.083"), BigDecimal.valueOf(100), new BigDecimal("120.000")));

        assertThat(orderSummary.edgeSummary().size()).isEqualTo(1);
        assertThat(orderSummary.edgeSummary().getFirst()).usingRecursiveComparison().isEqualTo(new OrderEdgeSummaryData(1L, new BigDecimal("2.620"), new BigDecimal("2.300"), new BigDecimal("2.620"), new BigDecimal("262.000"), new BigDecimal("314.400"), new BigDecimal("2.300"), new BigDecimal("2.760")));

        assertThat(orderSummary.glueSummary()).usingRecursiveComparison().isEqualTo(new OrderGlueSummaryData(BigDecimal.valueOf(0), BigDecimal.valueOf(0), new BigDecimal("0.000")));

        assertThat(orderSummary.cutSummary().size()).isEqualTo(1);
        assertThat(orderSummary.cutSummary().getFirst()).usingRecursiveComparison().isEqualTo(new OrderCutSummaryData(BigDecimal.valueOf(10), new BigDecimal("2.300"), new BigDecimal("2.300"), new BigDecimal("2.760")));

        assertThat(orderSummary.weight()).isEqualTo(new BigDecimal("2.703"));
        assertThat(orderSummary.total()).isEqualTo(new BigDecimal("366.600"));
        assertThat(orderSummary.vatTotal()).isEqualTo(new BigDecimal("439.920"));
    }
}
