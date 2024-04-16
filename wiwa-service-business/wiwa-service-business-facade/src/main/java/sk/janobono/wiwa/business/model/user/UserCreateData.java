package sk.janobono.wiwa.business.model.user;

import sk.janobono.wiwa.model.Authority;

import java.util.List;

public record UserCreateData(
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
        List<Authority> authorities
) {
}
