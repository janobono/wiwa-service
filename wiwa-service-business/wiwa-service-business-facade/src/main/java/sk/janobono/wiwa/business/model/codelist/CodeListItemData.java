package sk.janobono.wiwa.business.model.codelist;

public record CodeListItemData(
        Long id,
        Long codeListId,
        Integer sortNum,
        String code,
        String value,
        boolean leafNode
) {
}
