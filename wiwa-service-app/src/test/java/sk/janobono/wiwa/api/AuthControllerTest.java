package sk.janobono.wiwa.api;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import sk.janobono.wiwa.BaseIntegrationTest;
import sk.janobono.wiwa.business.model.auth.*;
import sk.janobono.wiwa.common.component.Captcha;
import sk.janobono.wiwa.common.model.WiwaProperty;
import sk.janobono.wiwa.dal.repository.ApplicationPropertyRepository;

import java.text.MessageFormat;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class AuthControllerTest extends BaseIntegrationTest {

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
        log.debug("sign up token = {}", token);

        AuthenticationResponseSo authenticationResponseDto = confirm(token);
        log.debug("confirm01 = {}", authenticationResponseDto);

        resendConfirmation(authenticationResponseDto);

        authenticationResponseDto = signIn(USERNAME, PASSWORD);
        log.debug("sign in = {}", authenticationResponseDto);

        authenticationResponseDto = changeEmail(authenticationResponseDto);
        log.debug("change email = {}", authenticationResponseDto);

        authenticationResponseDto = changePassword(authenticationResponseDto);
        log.debug("change password = {}", authenticationResponseDto);

        authenticationResponseDto = changeUserDetails(authenticationResponseDto);
        log.debug("change user details = {}", authenticationResponseDto);

        String[] data = resetPassword();
        token = data[0];
        log.debug("reset password token = {}", token);
        authenticationResponseDto = confirm(token);
        log.debug("confirm02 = {}", authenticationResponseDto);

        authenticationResponseDto = signIn(USERNAME, data[1]);
        log.debug("sign in = {}", authenticationResponseDto);

        authenticationResponseDto = refresh(authenticationResponseDto.refreshToken());
        log.debug("refresh = {}", authenticationResponseDto);
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

        String captchaText = captcha.generateText();
        String captchaToken = captcha.generateToken(captchaText);

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

        TestEmail testEmail = getAllEmails()[0];
        return getSubstring(testEmail, "confirm/", ">"
                        + applicationPropertyRepository.getApplicationProperty(
                        WiwaProperty.AUTH_SIGN_UP_MAIL_LINK.getGroup(),
                        WiwaProperty.AUTH_SIGN_UP_MAIL_LINK.getKey(),
                        "en"
                ).orElseThrow().value()
        ).trim().replaceAll("\"", "");
    }

    private void resendConfirmation(AuthenticationResponseSo authenticationResponseSO) throws Exception {
        deleteAllEmails();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authenticationResponseSO.token());

        String captchaText = captcha.generateText();
        String captchaToken = captcha.generateToken(captchaText);

        restTemplate.exchange(
                getURI("/auth/resend-confirmation"),
                HttpMethod.POST,
                new HttpEntity<>(new ResendConfirmationSo(
                        captchaText,
                        captchaToken
                ), headers),
                AuthenticationResponseSo.class
        );

        TestEmail testEmail = getAllEmails()[0];
        String token = getSubstring(testEmail, "confirm/", ">"
                        + applicationPropertyRepository.getApplicationProperty(
                        WiwaProperty.AUTH_SIGN_UP_MAIL_LINK.getGroup(),
                        WiwaProperty.AUTH_SIGN_UP_MAIL_LINK.getKey(),
                        "en"
                ).orElseThrow().value()
        ).trim().replaceAll("\"", "");
        assertThat(token).isNotEmpty();
    }

    private AuthenticationResponseSo confirm(String token) throws Exception {
        return restTemplate.postForObject(
                getURI("/auth/confirm"),
                new ConfirmationSo(
                        token
                ),
                AuthenticationResponseSo.class
        );
    }

    private AuthenticationResponseSo refresh(String token) throws Exception {
        return restTemplate.postForObject(
                getURI("/auth/refresh"),
                new RefreshTokenSo(
                        token
                ),
                AuthenticationResponseSo.class
        );
    }

    private AuthenticationResponseSo changeEmail(AuthenticationResponseSo authenticationResponseSO) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authenticationResponseSO.token());

        String captchaText = captcha.generateText();
        String captchaToken = captcha.generateToken(captchaText);

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

        ResponseEntity<AuthenticationResponseSo> response = restTemplate.exchange(
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

    private AuthenticationResponseSo changePassword(AuthenticationResponseSo authenticationResponseSO) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authenticationResponseSO.token());

        String captchaText = captcha.generateText();
        String captchaToken = captcha.generateToken(captchaText);

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

        ResponseEntity<AuthenticationResponseSo> response = restTemplate.exchange(
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

    private AuthenticationResponseSo changeUserDetails(AuthenticationResponseSo authenticationResponseSO) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authenticationResponseSO.token());

        String captchaText = captcha.generateText();
        String captchaToken = captcha.generateToken(captchaText);

        ResponseEntity<AuthenticationResponseSo> response = restTemplate.exchange(
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

        String captchaText = captcha.generateText();
        String captchaToken = captcha.generateToken(captchaText);

        restTemplate.postForObject(
                getURI("/auth/reset-password"),
                new ResetPasswordSo(
                        EMAIL,
                        captchaText,
                        captchaToken
                ),
                Void.class
        );

        TestEmail testEmail = getAllEmails()[0];
        return new String[]{
                getSubstring(testEmail, "confirm/", ">"
                                + applicationPropertyRepository.getApplicationProperty(
                                WiwaProperty.AUTH_RESET_PASSWORD_MAIL_LINK.getGroup(),
                                WiwaProperty.AUTH_RESET_PASSWORD_MAIL_LINK.getKey(),
                                "en"
                        ).orElseThrow().value()
                ).trim().replaceAll("\"", ""),
                getSubstring(testEmail,
                        MessageFormat.format(
                                applicationPropertyRepository.getApplicationProperty(
                                        WiwaProperty.AUTH_RESET_PASSWORD_MAIL_PASSWORD_MESSAGE.getGroup(),
                                        WiwaProperty.AUTH_RESET_PASSWORD_MAIL_PASSWORD_MESSAGE.getKey(),
                                        "en"
                                ).orElseThrow().value()
                                , ""),
                        "<footer>")
                        .replaceAll("</p>", "").replaceAll("</main>", "").trim()
        };
    }

    private String getSubstring(TestEmail testEmail, String startSequence, String endSequence) throws Exception {
        String result = testEmail.html();
        result = result.substring(
                result.indexOf(startSequence) + startSequence.length(),
                result.indexOf(endSequence)
        );
        return result;
    }
}
