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
    private BigDecimal weight;
    private BigDecimal total;
    private String data;
}
