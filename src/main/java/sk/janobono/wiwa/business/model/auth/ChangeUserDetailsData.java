package sk.janobono.wiwa.business.model.auth;

public record ChangeUserDetailsData(
        String titleBefore,
        String firstName,
        String midName,
        String lastName,
        String titleAfter,
        Boolean gdpr,
        String captchaText,
        String captchaToken
) {
}
