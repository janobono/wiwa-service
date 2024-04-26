package sk.janobono.wiwa.dal.domain;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class OrderItemSummaryDo {
    private Long orderItemId;
    private String code;
    private BigDecimal amount;
}
