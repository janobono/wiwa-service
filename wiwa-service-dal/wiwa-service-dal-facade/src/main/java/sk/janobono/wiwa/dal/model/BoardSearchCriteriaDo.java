package sk.janobono.wiwa.dal.model;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record BoardSearchCriteriaDo(
        String searchField,
        String code,
        String name,
        String boardCode,
        String structureCode,
        Boolean orientation,
        BigDecimal lengthFrom,
        BigDecimal lengthTo,
        BigDecimal widthFrom,
        BigDecimal widthTo,
        BigDecimal thicknessFrom,
        BigDecimal thicknessTo,
        BigDecimal priceFrom,
        BigDecimal priceTo,
        List<String> codeListItems
) {
}
