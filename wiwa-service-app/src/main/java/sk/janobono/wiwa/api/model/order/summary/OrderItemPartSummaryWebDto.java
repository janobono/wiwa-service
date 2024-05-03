package sk.janobono.wiwa.api.model.order.summary;

import java.math.BigDecimal;
import java.util.List;

public record OrderItemPartSummaryWebDto(
        List<OrderItemBoardSummaryWebDto> boardSummary,
        List<OrderItemEdgeSummaryWebDto> edgeSummary,
        BigDecimal gluedArea,
        List<OrderItemCutSummaryWebDto> cutSummary
) {
}
