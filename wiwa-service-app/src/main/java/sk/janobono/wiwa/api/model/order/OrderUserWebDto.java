package sk.janobono.wiwa.api.model.order;

public record OrderUserWebDto(
        Long id,
        String titleBefore,
        String firstName,
        String midName,
        String lastName,
        String titleAfter,
        String email
) {
}
