package sk.janobono.wiwa.dal.domain;

import lombok.Builder;
import lombok.Data;
import sk.janobono.wiwa.model.ProductAttributeKey;

@Builder
@Data
public class ProductAttributeDo {
    private Long id;
    private Long productId;
    private ProductAttributeKey key;
    private String value;
}
