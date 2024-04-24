package sk.janobono.wiwa.business.model.order;

import sk.janobono.wiwa.business.model.order.part.PartChangeData;
import sk.janobono.wiwa.model.PartType;

public record OrderItemChangeData(
        String name,
        PartType type,
        PartChangeData partChangeData,
        Integer amount
) {
}
