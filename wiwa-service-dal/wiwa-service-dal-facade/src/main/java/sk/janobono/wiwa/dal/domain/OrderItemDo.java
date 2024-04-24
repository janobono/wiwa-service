package sk.janobono.wiwa.dal.domain;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class OrderItemDo {
    private Long id;
    private Long orderId;
    private Integer sortNum;
    private String name;
    private BigDecimal partPrice;
    private BigDecimal partNetWeight;
    private BigDecimal amount;
    private BigDecimal netWeight;
    private BigDecimal total;
    private String data;
}
