package sk.janobono.wiwa.business.model.order;

import lombok.Builder;

@Builder
public record OrderUserData(
        Long id,
        String titleBefore,
        String firstName,
        String midName,
        String lastName,
        String titleAfter,
        String email
) {
}
