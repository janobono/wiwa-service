package sk.janobono.wiwa.business.model.order;

import lombok.Builder;
import sk.janobono.wiwa.business.model.order.part.PartData;
import sk.janobono.wiwa.model.Money;
import sk.janobono.wiwa.model.Quantity;

@Builder
public record OrderItemData(
        Long id,
        Integer sortNum,
        String name,
        Money partPrice,
        Money vatPartPrice,
        Quantity partNetWeight,
        Quantity amount,
        Quantity netWeight,
        Money total,
        Money vatTotal,
        PartData partData
) {
}
