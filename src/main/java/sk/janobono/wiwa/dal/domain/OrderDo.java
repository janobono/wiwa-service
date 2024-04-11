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
    private String creator;
    private LocalDateTime created;
    private String modifier;
    private LocalDateTime modified;
    private String name;
    private OrderStatus status;
    private BigDecimal totalValue;
    private Unit totalUnit;
}
