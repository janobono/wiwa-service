package sk.janobono.wiwa.api.model.user;

import sk.janobono.wiwa.model.Authority;

import java.util.List;

public record UserWebDto(
        Long id,
        String username,
        String titleBefore,
        String firstName,
        String midName,
        String lastName,
        String titleAfter,
        String email,
        Boolean gdpr,
        Boolean confirmed,
        Boolean enabled,
        List<Authority> authorities,
        List<Long> codeListItems
) {
}
