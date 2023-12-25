package sk.janobono.wiwa.dal.domain;

import lombok.Builder;
import lombok.Data;
import sk.janobono.wiwa.model.ProductStockStatus;

@Builder
@Data
public class ProductDo {
    private Long id;
    private String code;
    private String name;
    private String description;
    private ProductStockStatus stockStatus;
}
