package sk.janobono.wiwa.business.model.board;

import lombok.Builder;
import sk.janobono.wiwa.model.Money;
import sk.janobono.wiwa.model.Quantity;
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
        Quantity lengthFrom,
        Quantity lengthTo,
        Quantity widthFrom,
        Quantity widthTo,
        Quantity thicknessFrom,
        Quantity thicknessTo,
        Money priceFrom,
        Money priceTo,
        List<String> codeListItems
) {
}
