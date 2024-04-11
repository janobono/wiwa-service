package sk.janobono.wiwa.dal.domain;

import lombok.Builder;
import lombok.Data;
import sk.janobono.wiwa.model.OrderDataKey;

@Builder
@Data
public class OrderDataDo {
    private Long id;
    private Long productId;
    private OrderDataKey key;
    private String value;
}
