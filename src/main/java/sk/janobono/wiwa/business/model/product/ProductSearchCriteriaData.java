package sk.janobono.wiwa.business.model.product;

import lombok.Builder;
import sk.janobono.wiwa.model.ProductStockStatus;

import java.util.List;

@Builder
public record ProductSearchCriteriaData(
        String searchField,
        String code,
        String name,
        ProductStockStatus stockStatus,
        List<String> codeListItems
) {
}
