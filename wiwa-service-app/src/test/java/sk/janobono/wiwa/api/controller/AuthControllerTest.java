package sk.janobono.wiwa.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sk.janobono.wiwa.BaseTest;
import sk.janobono.wiwa.api.model.auth.*;
import sk.janobono.wiwa.business.impl.model.mail.MailData;
import sk.janobono.wiwa.business.service.ApplicationPropertyService;
import sk.janobono.wiwa.component.Captcha;
import sk.janobono.wiwa.config.CommonConfigProperties;

import static org.assertj.core.api.Assertions.assertThat;

class AuthControllerTest extends BaseTest {

    public static final String USERNAME = "jimbop";
    public static final String TITLE_BEFORE = "Phdr.";
    public static final String FIRST_NAME = "Jimbo";
    public static final String MID_NAME = "Lol";
    public static final String LAST_NAME = "Pytlik";
    public static final String TITLE_AFTER = "Csc.";
    public static final String EMAIL = "jimbo.pytlik@domain.com";
    public static final String NEW_PASSWORD = "newPass123";

    @Autowired
    public CommonConfigProperties commonConfigProperties;

    @Autowired
    public Captcha captcha;

    @Autowired
    public ApplicationPropertyService applicationPropertyService;

    @Test
    public void signInTest() {
        signIn(DEFAULT_ADMIN, PASSWORD);
        signIn(DEFAULT_MANAGER, PASSWORD);
        signIn(DEFAULT_EMPLOYEE, PASSWORD);
        signIn(DEFAULT_CUSTOMER, PASSWORD);
    }

    @Test
    void fullTest() {
        applicationPropertyService.setMaintenance(false);

        String token = signUp();
        AuthenticationResponseWebDto authenticationResponse = confirm(token);
        resendConfirmation(authenticationResponse);
        authenticationResponse = signIn(USERNAME, PASSWORD);
        authenticationResponse = changeEmail(authenticationResponse);
        authenticationResponse = changePassword(authenticationResponse);
        authenticationResponse = changeUserDetails(authenticationResponse);
        final String[] data = resetPassword();
        token = data[0];
        authenticationResponse = confirm(token);
        authenticationResponse = signIn(USERNAME, data[1]);
        authenticationResponse = refresh(authenticationResponse.refreshToken());
    }

    private String signUp() {
        final String captchaText = captcha.generateText();
        final String captchaToken = captcha.generateToken(captchaText);

        restClient.post()
                .uri(getURI("/auth/sign-up"))
                .body(new SignUpWebDto(
                        USERNAME,
                        PASSWORD,
                        TITLE_BEFORE,
                        FIRST_NAME,
                        MID_NAME,
                        LAST_NAME,
                        TITLE_AFTER,
                        EMAIL,
                        true,
                        captchaText,
                        captchaToken
                ))
                .retrieve();

        final MailData mailData = testMail.getMail();
        assertThat(mailData).isNotNull();
        assertThat(mailData.content()).isNotNull();
        assertThat(mailData.content().mailLink()).isNotNull();
        assertThat(mailData.content().mailLink().href()).isNotBlank();
        final String regex = commonConfigProperties.webUrl() + "/ui/confirm/";
        return mailData.content().mailLink().href().replaceAll(regex, "");
    }

    private AuthenticationResponseWebDto confirm(final String token) {
        return restClient.post()
                .uri(getURI("/auth/confirm"))
                .body(new ConfirmationWebDto(token))
                .retrieve()
                .body(AuthenticationResponseWebDto.class);
    }

    private void resendConfirmation(final AuthenticationResponseWebDto authenticationResponse) {
        final String captchaText = captcha.generateText();
        final String captchaToken = captcha.generateToken(captchaText);

        restClient.post()
                .uri(getURI("/auth/resend-confirmation"))
                .header("Authorization", "Bearer " + authenticationResponse.token())
                .body(new ResendConfirmationWebDto(captchaText, captchaToken))
                .retrieve();

        final MailData mailData = testMail.getMail();
        assertThat(mailData).isNotNull();
        assertThat(mailData.content()).isNotNull();
        assertThat(mailData.content().mailLink()).isNotNull();
        assertThat(mailData.content().mailLink().href()).isNotBlank();
        final String regex = commonConfigProperties.webUrl() + "/ui/confirm/";
        final String token = mailData.content().mailLink().href().replaceAll(regex, "");
        assertThat(token).isNotEmpty();
    }

    private AuthenticationResponseWebDto changeEmail(final AuthenticationResponseWebDto authenticationResponse) {
        final String captchaText = captcha.generateText();
        final String captchaToken = captcha.generateToken(captchaText);

        restClient.post()
                .uri(getURI("/auth/change-email"))
                .header("Authorization", "Bearer " + authenticationResponse.token())
                .body(new ChangeEmailWebDto("a" + EMAIL, PASSWORD, captchaText, captchaToken))
                .retrieve();

        return restClient.post()
                .uri(getURI("/auth/change-email"))
                .header("Authorization", "Bearer " + authenticationResponse.token())
                .body(new ChangeEmailWebDto(EMAIL, PASSWORD, captchaText, captchaToken))
                .retrieve()
                .body(AuthenticationResponseWebDto.class);
    }

    private AuthenticationResponseWebDto changePassword(final AuthenticationResponseWebDto authenticationResponse) {
        final String captchaText = captcha.generateText();
        final String captchaToken = captcha.generateToken(captchaText);

        restClient.post()
                .uri(getURI("/auth/change-password"))
                .header("Authorization", "Bearer " + authenticationResponse.token())
                .body(new ChangePasswordWebDto(PASSWORD, NEW_PASSWORD, captchaText, captchaToken))
                .retrieve();

        return restClient.post()
                .uri(getURI("/auth/change-password"))
                .header("Authorization", "Bearer " + authenticationResponse.token())
                .body(new ChangePasswordWebDto(NEW_PASSWORD, PASSWORD, captchaText, captchaToken))
                .retrieve()
                .body(AuthenticationResponseWebDto.class);
    }

    private AuthenticationResponseWebDto changeUserDetails(final AuthenticationResponseWebDto authenticationResponse) {
        final String captchaText = captcha.generateText();
        final String captchaToken = captcha.generateToken(captchaText);

        return restClient.post()
                .uri(getURI("/auth/change-user-details"))
                .header("Authorization", "Bearer " + authenticationResponse.token())
                .body(new ChangeUserDetailsWebDto(
                        TITLE_BEFORE,
                        FIRST_NAME,
                        MID_NAME,
                        LAST_NAME,
                        TITLE_AFTER,
                        true,
                        captchaText,
                        captchaToken
                ))
                .retrieve()
                .body(AuthenticationResponseWebDto.class);
    }

    private String[] resetPassword() {
        final String captchaText = captcha.generateText();
        final String captchaToken = captcha.generateToken(captchaText);

        restClient.post()
                .uri(getURI("/auth/reset-password"))
                .body(new ResetPasswordWebDto(EMAIL, captchaText, captchaToken))
                .retrieve();

        final MailData mailData = testMail.getMail();
        assertThat(mailData).isNotNull();
        assertThat(mailData.content()).isNotNull();
        assertThat(mailData.content().mailLink()).isNotNull();
        assertThat(mailData.content().mailLink().href()).isNotBlank();
        final String regex = commonConfigProperties.webUrl() + "/ui/confirm/";
        final String token = mailData.content().mailLink().href().replaceAll(regex, "");
        final String password = mailData.content().lines().getLast().replaceAll("New password: ", "");
        return new String[]{token, password};
    }

    private AuthenticationResponseWebDto refresh(final String token) {
        return restClient.post()
                .uri(getURI("/auth/refresh"))
                .body(new RefreshTokenWebDto(token))
                .retrieve()
                .body(AuthenticationResponseWebDto.class);
    }
}
