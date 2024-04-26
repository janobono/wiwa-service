package sk.janobono.wiwa.api.model.order;

import sk.janobono.wiwa.api.model.order.part.PartWebDto;
import sk.janobono.wiwa.api.model.order.summary.OrderItemSummaryWebDto;

public record OrderItemWebDto(
        Long id,
        Integer sortNum,
        String name,
        Integer quantity,
        PartWebDto part,
        OrderItemSummaryWebDto summary
) {
}
