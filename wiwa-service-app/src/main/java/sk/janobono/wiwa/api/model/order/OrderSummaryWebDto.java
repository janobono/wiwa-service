package sk.janobono.wiwa.api.model.order;

import java.util.Map;

public record OrderSummaryWebDto(
        Map<Long, OrderBoardWebDto> boards,
        Map<Long, OrderEdgeWebDto> edges
) {
}
