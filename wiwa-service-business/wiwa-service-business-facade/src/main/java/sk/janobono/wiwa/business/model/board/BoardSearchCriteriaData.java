package sk.janobono.wiwa.business.model.board;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record BoardSearchCriteriaData(
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
