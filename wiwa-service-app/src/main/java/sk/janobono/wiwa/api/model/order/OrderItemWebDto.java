package sk.janobono.wiwa.api.model.order;

import sk.janobono.wiwa.model.Money;
import sk.janobono.wiwa.model.Quantity;

public record OrderItemWebDto(
        Long id,
        Integer sortNum,
        String name,
        Money partPrice,
        Money vatPartPrice,
        Quantity partWeight,
        Quantity amount,
        Quantity weight,
        Money total,
        Money vatTotal
) {
}
