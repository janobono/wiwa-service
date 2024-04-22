package sk.janobono.wiwa.business.model.order;

import lombok.Builder;
import sk.janobono.wiwa.model.Money;
import sk.janobono.wiwa.model.OrderStatus;
import sk.janobono.wiwa.model.Quantity;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record OrderData(
        Long id,
        OrderUserData creator,
        LocalDateTime created,
        OrderStatus status,
        Long orderNumber,
        Quantity netWeight,
        Money total,
        Money vatTotal,
        LocalDate deliveryDate,
        LocalDateTime ready,
        LocalDateTime finished
) {
}
