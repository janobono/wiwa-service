package sk.janobono.wiwa.api.model.product;

import sk.janobono.wiwa.api.model.ApplicationImageInfoWebDto;
import sk.janobono.wiwa.model.ProductStockStatus;

import java.util.List;

public record ProductWebDto(
        Long id,
        String code,
        String name,
        String description,
        ProductStockStatus stockStatus,
        List<ProductAttributeWebDto> attributes,
        List<ApplicationImageInfoWebDto> images,
        List<ProductQuantityWebDto> quantities,
        List<ProductUnitPriceWebDto> unitPrices,
        List<ProductCategoryItemWebDto> categoryItems
) {
}
