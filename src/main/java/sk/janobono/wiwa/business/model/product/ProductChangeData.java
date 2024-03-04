package sk.janobono.wiwa.business.model.product;

import sk.janobono.wiwa.model.ProductStockStatus;

import java.util.List;

public record ProductChangeData(
        String code,
        String name,
        String description,
        ProductStockStatus stockStatus,
        List<ProductAttributeData> attributes,
        List<ProductQuantityData> quantities
) {
}
