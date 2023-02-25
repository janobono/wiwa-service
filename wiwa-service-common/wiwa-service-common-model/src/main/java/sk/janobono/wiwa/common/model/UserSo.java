package sk.janobono.wiwa.common.model;

import java.util.Set;

public record UserSo(
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
