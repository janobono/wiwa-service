package sk.janobono.wiwa.api.model.order;

import sk.janobono.wiwa.api.model.QuantityWebDto;
import sk.janobono.wiwa.model.Money;

public record OrderItemWebDto(
        Long id,
        Integer sortNum,
        String name,
        Money partPrice,
        QuantityWebDto partNetWeight,
        QuantityWebDto amount,
        QuantityWebDto netWeight,
        Money total
) {
}
