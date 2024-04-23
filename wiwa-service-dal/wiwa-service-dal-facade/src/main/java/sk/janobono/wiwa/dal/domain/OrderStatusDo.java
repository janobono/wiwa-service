package sk.janobono.wiwa.dal.domain;

import lombok.Builder;
import lombok.Data;
import sk.janobono.wiwa.model.OrderStatus;

import java.time.LocalDateTime;

@Builder
@Data
public class OrderStatusDo {
    private Long id;
    private Long orderId;
    private Long userId;
    private LocalDateTime created;
    private OrderStatus status;
    private String comment;
}
