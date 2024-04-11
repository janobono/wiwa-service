package sk.janobono.wiwa.dal.domain;

import lombok.Builder;
import lombok.Data;
import sk.janobono.wiwa.model.OrderItemDataKey;

@Builder
@Data
public class OrderItemDataDo {
    private Long id;
    private Long productId;
    private OrderItemDataKey key;
    private String value;
}
