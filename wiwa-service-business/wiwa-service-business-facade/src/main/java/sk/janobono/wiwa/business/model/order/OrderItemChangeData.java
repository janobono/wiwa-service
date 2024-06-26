package sk.janobono.wiwa.business.model.order;

import lombok.Builder;
import sk.janobono.wiwa.business.model.order.part.PartData;

@Builder
public record OrderItemChangeData(
        String name,
        String description,
        Integer quantity,
        PartData part
) {
}
