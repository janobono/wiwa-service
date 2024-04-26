package sk.janobono.wiwa.dal.domain;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class OrderMaterialDo {
    private Long orderId;
    private Long materialId;
    private String code;
    private String data;
}
