package sk.janobono.wiwa.dal.model;

import sk.janobono.wiwa.model.Money;
import sk.janobono.wiwa.model.Quantity;

import java.util.List;

public record EdgeSearchCriteriaDo(
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
