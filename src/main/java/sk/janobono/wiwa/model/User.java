package sk.janobono.wiwa.model;

import java.util.Set;

public record User(
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
        Set<Authority> authorities
) {
}
