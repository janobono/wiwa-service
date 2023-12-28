package sk.janobono.wiwa.dal.domain;

import lombok.Builder;
import lombok.Data;
import sk.janobono.wiwa.model.ProductQuantityKey;
import sk.janobono.wiwa.model.Unit;

import java.math.BigDecimal;

@Builder
@Data
public class ProductQuantityDo {
    private Long id;
    private Long productId;
    private ProductQuantityKey key;
    private BigDecimal value;
    private Unit unit;
}
