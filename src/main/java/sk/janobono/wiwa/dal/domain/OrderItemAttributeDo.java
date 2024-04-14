package sk.janobono.wiwa.dal.domain;

import lombok.Builder;
import lombok.Data;
import sk.janobono.wiwa.model.OrderItemAttributeKey;

@Builder
@Data
public class OrderItemAttributeDo {
    private Long orderItemId;
    private OrderItemAttributeKey attributeKey;
    private String attributeValue;
}
