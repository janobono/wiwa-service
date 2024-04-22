package sk.janobono.wiwa.dal.domain;

import lombok.Builder;
import lombok.Data;
import sk.janobono.wiwa.model.Money;
import sk.janobono.wiwa.model.OrderStatus;
import sk.janobono.wiwa.model.Quantity;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Data
public class OrderDo {
    private Long id;
    private Long userId;
    private LocalDateTime created;
    private OrderStatus status;
    private Long orderNumber;
    private Quantity netWeight;
    private Money total;
    private LocalDate deliveryDate;
    private LocalDateTime ready;
    private LocalDateTime finished;
}
