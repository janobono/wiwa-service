package sk.janobono.wiwa.business.model.codelist;

public record CodeListItemChangeData(Long codeListId, Long parentId, String code, String value) {
}
