package sk.janobono.wiwa.dal.domain;

import lombok.Builder;
import lombok.Data;
import sk.janobono.wiwa.model.OrderItemType;
import sk.janobono.wiwa.model.Unit;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Data
public class OrderItemDo {
    private Long id;
    private Long orderId;
    private Long parentId;
    private String creator;
    private LocalDateTime created;
    private String modifier;
    private LocalDateTime modified;
    private OrderItemType type;
    private String code;
    private String name;
    private BigDecimal priceValue;
    private Unit priceUnit;
    private BigDecimal amountValue;
    private Unit amountUnit;
    private BigDecimal totalValue;
    private Unit totalUnit;
    private String data;
}
