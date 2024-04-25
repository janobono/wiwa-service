package sk.janobono.wiwa.business.model.order;

import sk.janobono.wiwa.business.model.order.part.PartChangeData;

public record OrderItemChangeData(
        String name,
        Integer amount,
        PartChangeData partChangeData
) {
}
