package sk.janobono.wiwa.business.model.order;

import lombok.Builder;
import sk.janobono.wiwa.business.model.order.part.PartData;
import sk.janobono.wiwa.business.model.order.summary.OrderItemSummaryData;

@Builder
public record OrderItemData(
        Long id,
        Integer sortNum,
        String name,
        Integer quantity,
        PartData part,
        OrderItemSummaryData summary
) {
}
