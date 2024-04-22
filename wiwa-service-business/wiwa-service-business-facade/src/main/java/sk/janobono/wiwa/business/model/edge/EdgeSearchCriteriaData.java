package sk.janobono.wiwa.business.model.edge;

import lombok.Builder;
import sk.janobono.wiwa.model.Money;
import sk.janobono.wiwa.model.Quantity;

import java.util.List;

@Builder
public record EdgeSearchCriteriaData(
        String searchField,
        String code,
        String name,
        Quantity widthFrom,
        Quantity widthTo,
        Quantity thicknessFrom,
        Quantity thicknessTo,
        Money priceFrom,
        Money priceTo,
        List<String> codeListItems
) {
}
