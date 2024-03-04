package sk.janobono.wiwa.api.model.codelist;

public record CodeListItemWebDto(Long id, Long codeListId, String code, String value, boolean leafNode) {
}
