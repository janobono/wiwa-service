package sk.janobono.wiwa.api.model.order.item.part;

import sk.janobono.wiwa.api.model.order.OrderBoardWebDto;
import sk.janobono.wiwa.api.model.order.OrderEdgeWebDto;
import sk.janobono.wiwa.model.Money;
import sk.janobono.wiwa.model.Quantity;

import java.util.Map;

public record PartSummaryWebDto(
        String name,
        Map<Long, OrderBoardWebDto> boards,
        Map<Long, OrderEdgeWebDto> edges,
        Quantity partWeight,
        Money partPrice,
        Integer amount,
        Quantity weight,
        Money total
) {
}
