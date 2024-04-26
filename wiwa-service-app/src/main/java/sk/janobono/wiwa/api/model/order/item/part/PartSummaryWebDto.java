package sk.janobono.wiwa.api.model.order.item.part;

import sk.janobono.wiwa.api.model.order.OrderBoardWebDto;
import sk.janobono.wiwa.api.model.order.OrderEdgeWebDto;

import java.math.BigDecimal;
import java.util.Map;

public record PartSummaryWebDto(
        String name,
        Map<Long, OrderBoardWebDto> boards,
        Map<Long, OrderEdgeWebDto> edges,
        BigDecimal partWeight,
        BigDecimal partPrice,
        Integer amount,
        BigDecimal weight,
        BigDecimal total
) {
}
