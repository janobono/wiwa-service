package sk.janobono.wiwa.dal.model;

public record UserSearchCriteriaDo(
        String searchField,
        String username,
        String email
) {
}
