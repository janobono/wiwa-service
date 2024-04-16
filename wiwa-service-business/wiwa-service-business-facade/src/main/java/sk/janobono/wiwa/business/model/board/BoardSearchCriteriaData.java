package sk.janobono.wiwa.business.model.board;

import lombok.Builder;
import sk.janobono.wiwa.model.Unit;

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
