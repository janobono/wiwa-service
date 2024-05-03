package sk.janobono.wiwa.business.model.order.summary;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record OrderSummaryData(
        List<OrderBoardSummaryData> boardSummary,
        List<OrderEdgeSummaryData> edgeSummary,
        OrderGlueSummaryData glueSummary,
        List<OrderCutSummaryData> cutSummary,
        BigDecimal weight,
        BigDecimal total,
        BigDecimal vatTotal
) {
}
