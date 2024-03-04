package sk.janobono.wiwa.dal.model;

import java.util.List;

public record UserSearchCriteriaDo(
        String searchField,
        String username,
        String email
) {
}
