package sk.janobono.wiwa.business.model.product;

import lombok.Builder;
import sk.janobono.wiwa.model.ApplicationImage;
import sk.janobono.wiwa.model.ProductStockStatus;

import java.util.List;

@Builder
public record ProductSo(
        Long id,
        String code,
        String name,
        String note,
        ProductStockStatus stockStatus,
        List<ProductAttributeSo> attributes,
        List<ApplicationImage> images,
        List<ProductQuantitySo> quantities,
        List<ProductUnitPriceSo> unitPrices,
        List<Long> codeListItems
) {
}
