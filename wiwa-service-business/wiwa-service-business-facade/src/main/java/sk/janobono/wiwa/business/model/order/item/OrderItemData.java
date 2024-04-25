package sk.janobono.wiwa.business.model.order.item;

import lombok.Builder;
import sk.janobono.wiwa.business.model.order.item.part.PartData;

@Builder
public record OrderItemData(
        Long id,
        Integer sortNum,
        PartData part
) {
}
