package sk.janobono.wiwa.business.model.user;

import lombok.Builder;

import java.util.List;

@Builder
public record UserSearchCriteriaSo(
        String searchField,
        String username,
        String email,
        List<String> codeListItems
) {
}
