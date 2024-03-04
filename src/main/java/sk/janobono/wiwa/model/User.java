package sk.janobono.wiwa.model;

import lombok.Builder;

import java.util.List;

@Builder
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
        List<Authority> authorities
) {
}
