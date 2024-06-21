package sk.janobono.wiwa.dal.model;

import lombok.Builder;

@Builder
public record UserSearchCriteriaDo(
        String searchField,
        String username,
        String email,
        Boolean order
) {
}
