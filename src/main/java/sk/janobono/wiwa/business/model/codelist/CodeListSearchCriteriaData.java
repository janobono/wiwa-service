package sk.janobono.wiwa.business.model.codelist;

import lombok.Builder;

@Builder
public record CodeListSearchCriteriaData(
        String searchField,
        String code,
        String name
) {
}
