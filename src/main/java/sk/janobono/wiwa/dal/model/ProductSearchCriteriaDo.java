package sk.janobono.wiwa.dal.model;

import sk.janobono.wiwa.model.ProductStockStatus;

import java.util.List;

public record ProductSearchCriteriaDo(
        String searchField,
        String code,
        String name,
        ProductStockStatus stockStatus,
        List<String> codeListItems
) {
}
