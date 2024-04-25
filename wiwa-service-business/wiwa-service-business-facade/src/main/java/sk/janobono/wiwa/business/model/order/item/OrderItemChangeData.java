package sk.janobono.wiwa.business.model.order.item;

import sk.janobono.wiwa.business.model.order.item.part.PartChangeData;

public record OrderItemChangeData(
        String name,
        Integer amount,
        PartChangeData partChange
) {
}
