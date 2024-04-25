package sk.janobono.wiwa.api.model.order.item;

import sk.janobono.wiwa.api.model.order.item.part.PartWebDto;

public record OrderItemWebDto(
        Long id,
        Integer sortNum,
        PartWebDto part
) {
}
