package sk.janobono.wiwa.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import sk.janobono.wiwa.business.model.auth.*;
import sk.janobono.wiwa.component.Captcha;
import sk.janobono.wiwa.dal.repository.ApplicationPropertyRepository;
import sk.janobono.wiwa.model.WiwaProperty;

import java.text.MessageFormat;

import static org.assertj.core.api.Assertions.assertThat;

class AuthControllerTest extends BaseControllerTest {

    public static final String USERNAME = "jimbop";
    public static final String TITLE_BEFORE = "Phdr.";
    public static final String FIRST_NAME = "Jimbo";
    public static final String MID_NAME = "Lol";
    public static final String LAST_NAME = "Pytlik";
    public static final String TITLE_AFTER = "Csc.";
    public static final String EMAIL = "jimbo.pytlik@domain.com";
    public static final String NEW_PASSWORD = "newPass123";

    @Autowired
    public Captcha captcha;

    @Autowired
    public ApplicationPropertyRepository applicationPropertyRepository;

    @Test
    void fullTest() throws Exception {
        String token = signUp();
        AuthenticationResponseSo authenticationResponseDto = confirm(token);
        resendConfirmation(authenticationResponseDto);
        authenticationResponseDto = signIn(USERNAME, PASSWORD);
        authenticationResponseDto = changeEmail(authenticationResponseDto);
        authenticationResponseDto = changePassword(authenticationResponseDto);
        authenticationResponseDto = changeUserDetails(authenticationResponseDto);
        final String[] data = resetPassword();
        token = data[0];
        authenticationResponseDto = confirm(token);
        authenticationResponseDto = signIn(USERNAME, data[1]);
        authenticationResponseDto = refresh(authenticationResponseDto.refreshToken());
    }

    @Test
    public void defaultUsersTest() throws Exception {
        signIn(DEFAULT_ADMIN, PASSWORD);
        signIn(DEFAULT_MANAGER, PASSWORD);
        signIn(DEFAULT_EMPLOYEE, PASSWORD);
        signIn(DEFAULT_CUSTOMER, PASSWORD);
    }

    private String signUp() throws Exception {
        deleteAllEmails();

        final String captchaText = captcha.generateText();
        final String captchaToken = captcha.generateToken(captchaText);

        restTemplate.postForObject(
                getURI("/auth/sign-up"),
                new SignUpSo(
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
                ),
                AuthenticationResponseSo.class
        );

        final TestEmail testEmail = getAllEmails()[0];
        return getSubstring(testEmail, "confirm/", ">"
                        + applicationPropertyRepository.findByGroupAndKey(
                        WiwaProperty.AUTH_SIGN_UP_MAIL_LINK.getGroup(),
                        WiwaProperty.AUTH_SIGN_UP_MAIL_LINK.getKey()
                ).orElseThrow().getValue()
        ).trim().replaceAll("\"", "");
    }

    private void resendConfirmation(final AuthenticationResponseSo authenticationResponseSO) throws Exception {
        deleteAllEmails();

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authenticationResponseSO.token());

        final String captchaText = captcha.generateText();
        final String captchaToken = captcha.generateToken(captchaText);

        restTemplate.exchange(
                getURI("/auth/resend-confirmation"),
                HttpMethod.POST,
                new HttpEntity<>(new ResendConfirmationSo(
                        captchaText,
                        captchaToken
                ), headers),
                AuthenticationResponseSo.class
        );

        final TestEmail testEmail = getAllEmails()[0];
        final String token = getSubstring(testEmail, "confirm/", ">"
                        + applicationPropertyRepository.findByGroupAndKey(
                        WiwaProperty.AUTH_SIGN_UP_MAIL_LINK.getGroup(),
                        WiwaProperty.AUTH_SIGN_UP_MAIL_LINK.getKey()
                ).orElseThrow().getValue()
        ).trim().replaceAll("\"", "");
        assertThat(token).isNotEmpty();
    }

    private AuthenticationResponseSo confirm(final String token) throws Exception {
        return restTemplate.postForObject(
                getURI("/auth/confirm"),
                new ConfirmationSo(
                        token
                ),
                AuthenticationResponseSo.class
        );
    }

    private AuthenticationResponseSo refresh(final String token) throws Exception {
        return restTemplate.postForObject(
                getURI("/auth/refresh"),
                new RefreshTokenSo(
                        token
                ),
                AuthenticationResponseSo.class
        );
    }

    private AuthenticationResponseSo changeEmail(final AuthenticationResponseSo authenticationResponseSO) throws Exception {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authenticationResponseSO.token());

        final String captchaText = captcha.generateText();
        final String captchaToken = captcha.generateToken(captchaText);

        restTemplate.exchange(
                getURI("/auth/change-email"),
                HttpMethod.POST,
                new HttpEntity<>(new ChangeEmailSo(
                        "a" + EMAIL,
                        PASSWORD,
                        captchaText,
                        captchaToken
                ), headers),
                AuthenticationResponseSo.class
        );

        final ResponseEntity<AuthenticationResponseSo> response = restTemplate.exchange(
                getURI("/auth/change-email"),
                HttpMethod.POST,
                new HttpEntity<>(
                        new ChangeEmailSo(
                                EMAIL,
                                PASSWORD,
                                captchaText,
                                captchaToken
                        ), headers),
                AuthenticationResponseSo.class
        );
        return response.getBody();
    }

    private AuthenticationResponseSo changePassword(final AuthenticationResponseSo authenticationResponseSO) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authenticationResponseSO.token());

        final String captchaText = captcha.generateText();
        final String captchaToken = captcha.generateToken(captchaText);

        restTemplate.exchange(
                getURI("/auth/change-password"),
                HttpMethod.POST,
                new HttpEntity<>(new ChangePasswordSo(
                        PASSWORD,
                        NEW_PASSWORD,
                        captchaText,
                        captchaToken
                ), headers),
                AuthenticationResponseSo.class
        );

        final ResponseEntity<AuthenticationResponseSo> response = restTemplate.exchange(
                getURI("/auth/change-password"),
                HttpMethod.POST,
                new HttpEntity<>(new ChangePasswordSo(
                        NEW_PASSWORD,
                        PASSWORD,
                        captchaText,
                        captchaToken
                ), headers),
                AuthenticationResponseSo.class
        );
        return response.getBody();
    }

    private AuthenticationResponseSo changeUserDetails(final AuthenticationResponseSo authenticationResponseSO) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authenticationResponseSO.token());

        final String captchaText = captcha.generateText();
        final String captchaToken = captcha.generateToken(captchaText);

        final ResponseEntity<AuthenticationResponseSo> response = restTemplate.exchange(
                getURI("/auth/change-user-details"),
                HttpMethod.POST,
                new HttpEntity<>(new ChangeUserDetailsSo(
                        TITLE_BEFORE,
                        FIRST_NAME,
                        MID_NAME,
                        LAST_NAME,
                        TITLE_AFTER,
                        true,
                        captchaText,
                        captchaToken
                ), headers),
                AuthenticationResponseSo.class
        );
        return response.getBody();
    }

    private String[] resetPassword() throws Exception {
        deleteAllEmails();

        final String captchaText = captcha.generateText();
        final String captchaToken = captcha.generateToken(captchaText);

        restTemplate.postForObject(
                getURI("/auth/reset-password"),
                new ResetPasswordSo(
                        EMAIL,
                        captchaText,
                        captchaToken
                ),
                Void.class
        );

        final TestEmail testEmail = getAllEmails()[0];
        return new String[]{
                getSubstring(testEmail, "confirm/", ">"
                                + applicationPropertyRepository.findByGroupAndKey(
                                WiwaProperty.AUTH_RESET_PASSWORD_MAIL_LINK.getGroup(),
                                WiwaProperty.AUTH_RESET_PASSWORD_MAIL_LINK.getKey()
                        ).orElseThrow().getValue()
                ).trim().replaceAll("\"", ""),
                getSubstring(testEmail,
                        MessageFormat.format(
                                applicationPropertyRepository.findByGroupAndKey(
                                        WiwaProperty.AUTH_RESET_PASSWORD_MAIL_PASSWORD_MESSAGE.getGroup(),
                                        WiwaProperty.AUTH_RESET_PASSWORD_MAIL_PASSWORD_MESSAGE.getKey()
                                ).orElseThrow().getValue()
                                , ""),
                        "<footer>")
                        .replaceAll("</p>", "").replaceAll("</main>", "").trim()
        };
    }

    private String getSubstring(final TestEmail testEmail, final String startSequence, final String endSequence) throws Exception {
        String result = testEmail.html();
        result = result.substring(
                result.indexOf(startSequence) + startSequence.length(),
                result.indexOf(endSequence)
        );
        return result;
    }
}
