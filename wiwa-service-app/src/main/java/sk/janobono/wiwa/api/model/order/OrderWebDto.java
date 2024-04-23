package sk.janobono.wiwa.api.model.order;

import org.springframework.format.annotation.DateTimeFormat;
import sk.janobono.wiwa.api.model.QuantityWebDto;
import sk.janobono.wiwa.model.Money;
import sk.janobono.wiwa.model.OrderStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record OrderWebDto(
        Long id,
        OrderUserWebDto creator,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime created,
        OrderStatus status,
        Long orderNumber,
        QuantityWebDto netWeight,
        Money total,
        Money vatTotal,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate deliveryDate
) {
}
