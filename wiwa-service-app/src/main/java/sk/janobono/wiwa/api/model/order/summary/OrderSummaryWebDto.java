package sk.janobono.wiwa.api.model.order.summary;

import java.math.BigDecimal;
import java.util.List;

public record OrderSummaryWebDto(
        List<OrderBoardSummaryWebDto> boardSummary,
        List<OrderEdgeSummaryWebDto> edgeSummary,
        OrderGlueSummaryWebDto glueSummary,
        List<OrderCutSummaryWebDto> cutSummary,
        BigDecimal weight,
        BigDecimal total,
        BigDecimal vatTotal
) {
}
