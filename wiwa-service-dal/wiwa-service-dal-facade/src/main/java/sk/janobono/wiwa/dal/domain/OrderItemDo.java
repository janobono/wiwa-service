package sk.janobono.wiwa.dal.domain;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class OrderItemDo {
    private Long id;
    private Long orderId;
    private Integer sortNum;
    private String data;
}
