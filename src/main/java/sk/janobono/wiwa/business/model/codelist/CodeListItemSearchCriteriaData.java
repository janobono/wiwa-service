package sk.janobono.wiwa.business.model.codelist;

import lombok.Builder;

@Builder
public record CodeListItemSearchCriteriaData(
        Long codeListId,
        Boolean root,
        Long parentId,
        String searchField,
        String code,
        String value,
        String treeCode
) {
}
