package sk.janobono.wiwa.api.model.order;

import sk.janobono.wiwa.model.Money;
import sk.janobono.wiwa.model.OrderStatus;
import sk.janobono.wiwa.model.Quantity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record OrderWebDto(
        Long id,
        OrderUserWebDto creator,
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
