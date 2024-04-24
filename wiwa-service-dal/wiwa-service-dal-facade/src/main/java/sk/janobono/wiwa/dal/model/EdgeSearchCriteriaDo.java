package sk.janobono.wiwa.dal.model;

import java.math.BigDecimal;
import java.util.List;

public record EdgeSearchCriteriaDo(
        String searchField,
        String code,
        String name,
        BigDecimal widthFrom,
        BigDecimal widthTo,
        BigDecimal thicknessFrom,
        BigDecimal thicknessTo,
        BigDecimal priceFrom,
        BigDecimal priceTo,
        List<String> codeListItems
) {
}
