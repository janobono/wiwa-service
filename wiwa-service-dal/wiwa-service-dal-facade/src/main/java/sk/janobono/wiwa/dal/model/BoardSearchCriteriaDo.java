package sk.janobono.wiwa.dal.model;

import sk.janobono.wiwa.model.Quantity;

import java.math.BigDecimal;
import java.util.List;

public record BoardSearchCriteriaDo(
        String searchField,
        String code,
        String name,
        String boardCode,
        String structureCode,
        Boolean orientation,
        Quantity lengthFrom,
        Quantity lengthTo,
        Quantity widthFrom,
        Quantity widthTo,
        Quantity thicknessFrom,
        Quantity thicknessTo,
        BigDecimal priceFrom,
        BigDecimal priceTo,
        List<String> codeListItems
) {
}
