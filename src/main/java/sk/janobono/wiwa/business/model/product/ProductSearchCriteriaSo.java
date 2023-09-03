package sk.janobono.wiwa.business.model.product;

import lombok.Builder;
import sk.janobono.wiwa.model.ProductStockStatus;
import sk.janobono.wiwa.model.ProductType;
import sk.janobono.wiwa.model.Quantity;

import java.math.BigDecimal;

@Builder
public record ProductSearchCriteriaSo(
        String searchField,
        ProductType type,
        String code,
        String name,
        String categoryCode,
        String boardCode,
        String structureCode,
        ProductStockStatus productStockStatus,
        BigDecimal unitPriceFrom,
        BigDecimal unitPriceTo,
        Quantity thickness,
        Boolean orientation
) {
}
