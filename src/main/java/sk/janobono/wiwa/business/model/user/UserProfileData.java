package sk.janobono.wiwa.business.model.user;

public record UserProfileData(
        String titleBefore,
        String firstName,
        String midName,
        String lastName,
        String titleAfter
) {
}
