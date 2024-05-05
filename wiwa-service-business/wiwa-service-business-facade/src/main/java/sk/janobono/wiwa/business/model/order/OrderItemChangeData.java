package sk.janobono.wiwa.business.model.order;

import sk.janobono.wiwa.business.model.order.part.PartData;

public record OrderItemChangeData(
        String name,
        String description,
        Integer quantity,
        PartData part
) {
}
