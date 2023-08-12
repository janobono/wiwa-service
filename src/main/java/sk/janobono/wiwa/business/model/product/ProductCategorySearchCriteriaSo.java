package sk.janobono.wiwa.business.model.product;

import lombok.Builder;

@Builder
public record ProductCategorySearchCriteriaSo(
        Boolean rootCategories,
        Long parentCategoryId,
        String searchField,
        String code,
        String name,
        String treeCode
) {
}
