package sk.janobono.wiwa.business.model.auth;

import lombok.Builder;

@Builder
public record SignUpData(
        String username,
        String password,
        String titleBefore,
        String firstName,
        String midName,
        String lastName,
        String titleAfter,
        String email,
        Boolean gdpr,
        String captchaText,
        String captchaToken
) {
}
