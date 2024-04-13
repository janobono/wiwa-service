package sk.janobono.wiwa.dal.model;

import sk.janobono.wiwa.model.Unit;

import java.math.BigDecimal;
import java.util.List;

public record EdgeSearchCriteriaDo(
        String searchField,
        String code,
        String name,
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
