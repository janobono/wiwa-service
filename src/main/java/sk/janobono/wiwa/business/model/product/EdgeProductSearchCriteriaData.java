package sk.janobono.wiwa.business.model.product;

import lombok.Builder;
import sk.janobono.wiwa.model.ProductStockStatus;
import sk.janobono.wiwa.model.Unit;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record EdgeProductSearchCriteriaData(
        String searchField,
        String code,
        String name,
        ProductStockStatus stockStatus,
        BigDecimal widthFrom,
        BigDecimal widthTo,
        Unit widthUnit,
        BigDecimal thicknessFrom,
        BigDecimal thicknessTo,
        Unit thicknessUnit,
        BigDecimal priceFrom,
        BigDecimal priceTo,
        Unit priceUnit,
        List<String> codeListItems
) {
}
