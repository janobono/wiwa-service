package sk.janobono.wiwa.business.model.product;

import lombok.Builder;
import sk.janobono.wiwa.business.model.ApplicationImageInfoData;
import sk.janobono.wiwa.model.ProductStockStatus;

import java.util.List;

@Builder
public record ProductData(
        Long id,
        String code,
        String name,
        String description,
        ProductStockStatus stockStatus,
        List<ProductAttributeData> attributes,
        List<ApplicationImageInfoData> images,
        List<ProductQuantityData> quantities,
        List<ProductUnitPriceData> unitPrices,
        List<ProductCategoryItemData> categoryItems
) {
}
