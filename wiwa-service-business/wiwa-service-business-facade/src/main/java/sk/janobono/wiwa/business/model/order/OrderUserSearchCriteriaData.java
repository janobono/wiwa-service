package sk.janobono.wiwa.business.model.order;

import lombok.Builder;

@Builder
public record OrderUserSearchCriteriaData(String searchField, String email) {
}
