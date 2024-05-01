package sk.janobono.wiwa.business.model.order.summary;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record OrderItemPartSummaryData(
        List<OrderBoardSummaryData> boardSummary,
        List<OrderEdgeSummaryData> edgeSummary,
        BigDecimal gluedArea,
        List<OrderCutSummaryData> cutSummary
) {
}
