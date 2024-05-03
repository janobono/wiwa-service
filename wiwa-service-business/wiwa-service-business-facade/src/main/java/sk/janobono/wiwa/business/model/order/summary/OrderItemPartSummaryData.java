package sk.janobono.wiwa.business.model.order.summary;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record OrderItemPartSummaryData(
        List<OrderItemBoardSummaryData> boardSummary,
        List<OrderItemEdgeSummaryData> edgeSummary,
        BigDecimal gluedArea,
        List<OrderItemCutSummaryData> cutSummary
) {
}
