package sk.janobono.wiwa.dal.domain;

import lombok.Builder;
import lombok.Data;
import sk.janobono.wiwa.model.OrderStatus;
import sk.janobono.wiwa.model.Unit;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Data
public class OrderDo {
    private Long id;
    private Long userId;
    private LocalDateTime created;
    private OrderStatus status;
    private Long orderNumber;
    private String description;
    private BigDecimal weightValue;
    private Unit weightUnit;
    private BigDecimal netWeightValue;
    private Unit netWeightUnit;
    private BigDecimal totalValue;
    private Unit totalUnit;
}
