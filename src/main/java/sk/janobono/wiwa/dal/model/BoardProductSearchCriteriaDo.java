package sk.janobono.wiwa.dal.model;

import sk.janobono.wiwa.model.ProductStockStatus;
import sk.janobono.wiwa.model.Unit;

import java.math.BigDecimal;
import java.util.List;

public record BoardProductSearchCriteriaDo(
        String searchField,
        String code,
        String name,
        ProductStockStatus stockStatus,
        String boardCode,
        String structureCode,
        Boolean orientation,
        BigDecimal lengthFrom,
        BigDecimal lengthTo,
        Unit lengthUnit,
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
