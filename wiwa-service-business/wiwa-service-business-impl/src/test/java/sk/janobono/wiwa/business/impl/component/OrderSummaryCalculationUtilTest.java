package sk.janobono.wiwa.business.impl.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.janobono.wiwa.business.model.application.PriceForCuttingData;
import sk.janobono.wiwa.business.model.application.PriceForGluingEdgeData;
import sk.janobono.wiwa.business.model.application.PriceForGluingLayerData;
import sk.janobono.wiwa.business.model.order.OrderBoardData;
import sk.janobono.wiwa.business.model.order.OrderEdgeData;
import sk.janobono.wiwa.business.model.order.summary.*;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderSummaryCalculationUtilTest {

    private List<OrderBoardData> boards;
    private List<OrderEdgeData> edges;

    private OrderSummaryCalculationUtil orderSummaryCalculationUtil;

    @BeforeEach
    void setUp() {
        boards = List.of(
                OrderBoardData.builder()
                        .id(1L)
                        .orientation(false)
                        .weight(BigDecimal.valueOf(1))
                        .length(BigDecimal.valueOf(2000))
                        .width(BigDecimal.valueOf(1500))
                        .thickness(BigDecimal.valueOf(10))
                        .price(BigDecimal.valueOf(100))
                        .build(),
                OrderBoardData.builder()
                        .id(2L)
                        .orientation(false)
                        .weight(BigDecimal.valueOf(2))
                        .length(BigDecimal.valueOf(2000))
                        .width(BigDecimal.valueOf(1500))
                        .thickness(BigDecimal.valueOf(20))
                        .price(BigDecimal.valueOf(200))
                        .build(),
                OrderBoardData.builder()
                        .id(3L)
                        .orientation(false)
                        .weight(BigDecimal.valueOf(3))
                        .length(BigDecimal.valueOf(2000))
                        .width(BigDecimal.valueOf(1500))
                        .thickness(BigDecimal.valueOf(30))
                        .price(BigDecimal.valueOf(300))
                        .build()
        );

        edges = List.of(
                OrderEdgeData.builder()
                        .id(1L)
                        .weight(BigDecimal.ONE)
                        .width(BigDecimal.valueOf(18))
                        .price(BigDecimal.valueOf(100))
                        .build(),
                OrderEdgeData.builder()
                        .id(2L)
                        .weight(BigDecimal.TWO)
                        .width(BigDecimal.valueOf(28))
                        .price(BigDecimal.valueOf(200))
                        .build(),
                OrderEdgeData.builder()
                        .id(3L)
                        .weight(BigDecimal.valueOf(3))
                        .width(BigDecimal.valueOf(38))
                        .price(BigDecimal.valueOf(300))
                        .build()
        );

        orderSummaryCalculationUtil = new OrderSummaryCalculationUtil(new PriceUtil());
    }

    @Test
    void calculateOrderSummary_whenValidData_thenTheseResults() {
        final OrderSummaryData orderSummary = orderSummaryCalculationUtil.calculateOrderSummary(
                boards,
                edges,
                BigDecimal.valueOf(20),
                List.of(
                        new PriceForCuttingData(BigDecimal.valueOf(20), BigDecimal.valueOf(1)),
                        new PriceForCuttingData(BigDecimal.valueOf(30), BigDecimal.valueOf(2)),
                        new PriceForCuttingData(BigDecimal.valueOf(40), BigDecimal.valueOf(3))
                ),
                new PriceForGluingLayerData(BigDecimal.valueOf(1)),
                List.of(
                        new PriceForGluingEdgeData(BigDecimal.valueOf(20), BigDecimal.valueOf(1)),
                        new PriceForGluingEdgeData(BigDecimal.valueOf(30), BigDecimal.valueOf(2)),
                        new PriceForGluingEdgeData(BigDecimal.valueOf(40), BigDecimal.valueOf(3))
                ),
                OrderItemPartSummaryData.builder()
                        .boardSummary(List.of(
                                new OrderItemBoardSummaryData(1L, BigDecimal.valueOf(1)),
                                new OrderItemBoardSummaryData(2L, BigDecimal.valueOf(2)),
                                new OrderItemBoardSummaryData(3L, BigDecimal.valueOf(3))
                        ))
                        .edgeSummary(List.of(
                                new OrderItemEdgeSummaryData(1L, BigDecimal.valueOf(1), BigDecimal.valueOf(1)),
                                new OrderItemEdgeSummaryData(2L, BigDecimal.valueOf(2), BigDecimal.valueOf(2)),
                                new OrderItemEdgeSummaryData(3L, BigDecimal.valueOf(3), BigDecimal.valueOf(3))
                        ))
                        .gluedArea(BigDecimal.valueOf(1))
                        .cutSummary(List.of(
                                new OrderItemCutSummaryData(BigDecimal.valueOf(10), BigDecimal.valueOf(1)),
                                new OrderItemCutSummaryData(BigDecimal.valueOf(20), BigDecimal.valueOf(2)),
                                new OrderItemCutSummaryData(BigDecimal.valueOf(30), BigDecimal.valueOf(3))
                        ))
                        .build()
        );

        assertThat(orderSummary.boardSummary().size()).isEqualTo(3);
        assertThat(orderSummary.boardSummary().getFirst()).usingRecursiveComparison().isEqualTo(new OrderBoardSummaryData(1L, BigDecimal.valueOf(1), BigDecimal.valueOf(1), new BigDecimal("0.333"), BigDecimal.valueOf(100), new BigDecimal("120.000")));
        assertThat(orderSummary.boardSummary().get(1)).usingRecursiveComparison().isEqualTo(new OrderBoardSummaryData(2L, BigDecimal.valueOf(2), BigDecimal.valueOf(1), new BigDecimal("1.334"), BigDecimal.valueOf(200), new BigDecimal("240.000")));
        assertThat(orderSummary.boardSummary().getLast()).usingRecursiveComparison().isEqualTo(new OrderBoardSummaryData(3L, BigDecimal.valueOf(3), BigDecimal.valueOf(2), new BigDecimal("3.000"), BigDecimal.valueOf(600), new BigDecimal("720.000")));

        assertThat(orderSummary.edgeSummary().size()).isEqualTo(3);
        assertThat(orderSummary.edgeSummary().getFirst()).usingRecursiveComparison().isEqualTo(new OrderEdgeSummaryData(1L, BigDecimal.valueOf(1), BigDecimal.valueOf(1), BigDecimal.valueOf(1), BigDecimal.valueOf(100), new BigDecimal("120.000"), BigDecimal.valueOf(1), new BigDecimal("1.200")));
        assertThat(orderSummary.edgeSummary().get(1)).usingRecursiveComparison().isEqualTo(new OrderEdgeSummaryData(2L, BigDecimal.valueOf(2), BigDecimal.valueOf(2), BigDecimal.valueOf(4), BigDecimal.valueOf(400), new BigDecimal("480.000"), BigDecimal.valueOf(4), new BigDecimal("4.800")));
        assertThat(orderSummary.edgeSummary().getLast()).usingRecursiveComparison().isEqualTo(new OrderEdgeSummaryData(3L, BigDecimal.valueOf(3), BigDecimal.valueOf(3), BigDecimal.valueOf(9), BigDecimal.valueOf(900), new BigDecimal("1080.000"), BigDecimal.valueOf(9), new BigDecimal("10.800")));

        assertThat(orderSummary.glueSummary()).usingRecursiveComparison().isEqualTo(new OrderGlueSummaryData(BigDecimal.valueOf(1), BigDecimal.valueOf(1), new BigDecimal("1.200")));

        assertThat(orderSummary.cutSummary().size()).isEqualTo(3);
        assertThat(orderSummary.cutSummary().getFirst()).usingRecursiveComparison().isEqualTo(new OrderCutSummaryData(BigDecimal.valueOf(10), BigDecimal.valueOf(1), BigDecimal.valueOf(1), new BigDecimal("1.200")));
        assertThat(orderSummary.cutSummary().get(1)).usingRecursiveComparison().isEqualTo(new OrderCutSummaryData(BigDecimal.valueOf(20), BigDecimal.valueOf(2), BigDecimal.valueOf(2), new BigDecimal("2.400")));
        assertThat(orderSummary.cutSummary().getLast()).usingRecursiveComparison().isEqualTo(new OrderCutSummaryData(BigDecimal.valueOf(30), BigDecimal.valueOf(3), BigDecimal.valueOf(6), new BigDecimal("7.200")));

        assertThat(orderSummary.weight()).isEqualTo(new BigDecimal("18.667"));
        assertThat(orderSummary.total()).isEqualTo(new BigDecimal("2324"));
        assertThat(orderSummary.vatTotal()).isEqualTo(new BigDecimal("2788.800"));
    }
}
