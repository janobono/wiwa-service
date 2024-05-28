package sk.janobono.wiwa.dal.model;

import lombok.Builder;

@Builder
public record CodeListSearchCriteriaDo(
        String searchField,
        String code,
        String name
) {
}
