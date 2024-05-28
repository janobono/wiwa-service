package sk.janobono.wiwa.dal.model;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
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
