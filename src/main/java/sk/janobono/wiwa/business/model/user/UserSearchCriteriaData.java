package sk.janobono.wiwa.business.model.user;

import lombok.Builder;

@Builder
public record UserSearchCriteriaData(
        String searchField,
        String username,
        String email
) {
}
