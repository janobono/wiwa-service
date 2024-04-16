package sk.janobono.wiwa.dal.model;

public record CodeListItemSearchCriteriaDo(
        Long codeListId,
        Boolean root,
        Long parentId,
        String searchField,
        String code,
        String value,
        String treeCode
) {
}
