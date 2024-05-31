package sk.janobono.wiwa.business.impl.component;

import org.junit.jupiter.api.Test;
import sk.janobono.wiwa.business.model.order.summary.*;
import sk.janobono.wiwa.dal.domain.OrderItemSummaryDo;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderSummaryCodeMapperTest {

    @Test
    void toOrderItemSummaries_whenValidData_thenTheseResults() {
        final OrderSummaryCodeMapper orderSummaryCodeMapper = new OrderSummaryCodeMapper();

        final OrderItemSummaryData orderItemSummary = OrderItemSummaryData.builder()
                .partSummary(OrderItemPartSummaryData.builder()
                        .boardSummary(List.of(
                                new OrderItemBoardSummaryData(1L, BigDecimal.ZERO)
                        ))
                        .edgeSummary(List.of(
                                new OrderItemEdgeSummaryData(1L, BigDecimal.ZERO, BigDecimal.ZERO)
                        ))
                        .gluedArea(BigDecimal.ZERO)
                        .cutSummary(List.of(
                                new OrderItemCutSummaryData(BigDecimal.ZERO, BigDecimal.ZERO)
                        ))
                        .build())
                .totalSummary(OrderItemPartSummaryData.builder()
                        .boardSummary(List.of(
                                new OrderItemBoardSummaryData(1L, BigDecimal.ZERO)
                        ))
                        .edgeSummary(List.of(
                                new OrderItemEdgeSummaryData(1L, BigDecimal.ZERO, BigDecimal.ZERO)
                        ))
                        .gluedArea(BigDecimal.ZERO)
                        .cutSummary(List.of(
                                new OrderItemCutSummaryData(BigDecimal.ZERO, BigDecimal.ZERO)
                        ))
                        .build())
                .build();

        final List<OrderItemSummaryDo> summaries = orderSummaryCodeMapper.toOrderItemSummaries(1L, orderItemSummary);

        final OrderItemSummaryData orderItemSummary2 = orderSummaryCodeMapper.toOrderItemSummary(summaries);

        assertThat(orderItemSummary).usingRecursiveComparison().isEqualTo(orderItemSummary2);
    }
}
