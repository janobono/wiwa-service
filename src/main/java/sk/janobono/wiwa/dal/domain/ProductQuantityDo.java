package sk.janobono.wiwa.dal.domain;

import lombok.Builder;
import lombok.Data;
import sk.janobono.wiwa.model.ProductQuantityKey;

import java.math.BigDecimal;

@Builder
@Data
public class ProductQuantityDo {
    private Long id;
    private Long productId;
    private Long unitId;
    private ProductQuantityKey key;
    private BigDecimal value;
}
