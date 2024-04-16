package sk.janobono.wiwa.dal.domain;

import lombok.Builder;
import lombok.Data;
import sk.janobono.wiwa.model.OrderAttributeKey;

@Builder
@Data
public class OrderAttributeDo {
    private Long orderId;
    private OrderAttributeKey attributeKey;
    private String attributeValue;
}
