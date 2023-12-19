package sk.janobono.wiwa.business.model.codelist;

public record CodeListItemSo(Long id, Long codeListId, String code, String value, boolean leafNode) {
}
