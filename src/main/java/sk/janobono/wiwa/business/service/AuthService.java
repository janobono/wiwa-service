package sk.janobono.wiwa.business.service;

import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.business.model.auth.*;
import sk.janobono.wiwa.business.model.mail.MailContentData;
import sk.janobono.wiwa.business.model.mail.MailData;
import sk.janobono.wiwa.business.model.mail.MailLinkData;
import sk.janobono.wiwa.business.model.mail.MailTemplate;
import sk.janobono.wiwa.business.service.util.UserUtilService;
import sk.janobono.wiwa.component.*;
import sk.janobono.wiwa.config.AuthConfigProperties;
import sk.janobono.wiwa.config.CommonConfigProperties;
import sk.janobono.wiwa.dal.domain.AuthorityDo;
import sk.janobono.wiwa.dal.domain.UserDo;
import sk.janobono.wiwa.dal.repository.AuthorityRepository;
import sk.janobono.wiwa.dal.repository.UserRepository;
import sk.janobono.wiwa.exception.WiwaException;
import sk.janobono.wiwa.model.Authority;
import sk.janobono.wiwa.model.User;
import sk.janobono.wiwa.model.WiwaProperty;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final CommonConfigProperties appConfigProperties;
    private final AuthConfigProperties authConfigProperties;
    private final PasswordEncoder passwordEncoder;
    private final Captcha captcha;
    private final JwtToken jwtToken;
    private final RandomString randomString;
    private final ScDf scDf;
    private final MailService mailService;
    private final VerificationToken verificationToken;
    private final AuthorityRepository authorityRepository;
    private final UserRepository userRepository;
    private final ApplicationPropertyService applicationPropertyService;
    private final UserUtilService userUtilService;

    public AuthenticationResponseData confirm(final ConfirmationData confirmation) {
        final Map<String, String> data = verificationToken.parseToken(confirmation.token());
        final UserDo userDo = switch (AuthToken.valueOf(data.get(AuthTokenKey.TYPE.name()))) {
            case SIGN_UP -> signUp(
                    Long.valueOf(data.get(AuthTokenKey.USER_ID.name()))
            );
            case RESET_PASSWORD -> resetPassword(
                    Long.valueOf(data.get(AuthTokenKey.USER_ID.name())),
                    data.get(AuthTokenKey.NEW_PASSWORD.name())
            );
            default -> throw WiwaException.UNSUPPORTED_VALIDATION_TOKEN.exception("Unsupported validation token");
        };
        return createAuthenticationResponse(userDo);
    }

    public AuthenticationResponseData changeEmail(final User user, final ChangeEmailData changeEmail) {
        captcha.checkTokenValid(changeEmail.captchaText(), changeEmail.captchaToken());
        if (userRepository.existsByEmail(scDf.toStripAndLowerCase(changeEmail.email()))) {
            throw WiwaException.USER_EMAIL_IS_USED.exception("Email is used");
        }
        final UserDo userDo = userUtilService.getUserDo(user.id());
        userUtilService.checkEnabled(userDo);
        userUtilService.checkPassword(userDo, changeEmail.password());
        userDo.setEmail(scDf.toStripAndLowerCase(changeEmail.email()));
        return createAuthenticationResponse(userRepository.save(userDo));
    }

    public AuthenticationResponseData changePassword(final User user, final ChangePasswordData changePassword) {
        captcha.checkTokenValid(changePassword.captchaText(), changePassword.captchaToken());
        final UserDo userDo = userUtilService.getUserDo(user.id());
        userUtilService.checkEnabled(userDo);
        userUtilService.checkPassword(userDo, changePassword.oldPassword());
        userDo.setPassword(passwordEncoder.encode(changePassword.newPassword()));
        return createAuthenticationResponse(userRepository.save(userDo));
    }

    public AuthenticationResponseData changeUserDetails(final User user, final ChangeUserDetailsData changeUserDetails) {
        captcha.checkTokenValid(changeUserDetails.captchaText(), changeUserDetails.captchaToken());
        final UserDo userDo = userUtilService.getUserDo(user.id());
        userUtilService.checkEnabled(userDo);
        if (!changeUserDetails.gdpr()) {
            throw WiwaException.GDPR.exception("GDPR has to be confirmed");
        }
        userDo.setTitleBefore(changeUserDetails.titleBefore());
        userDo.setFirstName(changeUserDetails.firstName());
        userDo.setMidName(changeUserDetails.midName());
        userDo.setLastName(changeUserDetails.lastName());
        userDo.setTitleAfter(changeUserDetails.titleAfter());
        userDo.setGdpr(changeUserDetails.gdpr());

        return createAuthenticationResponse(userRepository.save(userDo));
    }

    public void resendConfirmation(final User user, final ResendConfirmationData resendConfirmation) {
        captcha.checkTokenValid(resendConfirmation.captchaText(), resendConfirmation.captchaToken());
        final UserDo userDo = userRepository.findById(user.id()).orElseThrow(
                () -> WiwaException.USER_NOT_FOUND.exception("User with username {0} not found", user.username())
        );
        sendSignUpMail(userDo);
    }

    public void resetPassword(final ResetPasswordData resetPassword) {
        captcha.checkTokenValid(resetPassword.captchaText(), resetPassword.captchaToken());
        final UserDo userDo = userRepository.findByEmail(scDf.toStripAndLowerCase(resetPassword.email())).orElseThrow(
                () -> WiwaException.USER_NOT_FOUND.exception("User with email {0} not found", resetPassword.email())
        );
        userUtilService.checkEnabled(userDo);
        sendResetPasswordMail(userDo);
    }

    public AuthenticationResponseData signIn(final SignInData signIn) {
        final UserDo userDo = userRepository.findByUsername(scDf.toStripAndLowerCase(signIn.username())).orElseThrow(
                () -> WiwaException.USER_NOT_FOUND.exception("User with username {0} not found", signIn.username())
        );
        userUtilService.checkEnabled(userDo);
        userUtilService.checkPassword(userDo, signIn.password());
        return createAuthenticationResponse(userDo);
    }

    public AuthenticationResponseData signUp(final SignUpData signUp) {
        captcha.checkTokenValid(signUp.captchaText(), signUp.captchaToken());
        if (userRepository.existsByUsername(scDf.toStripAndLowerCase(signUp.username()))) {
            throw WiwaException.USER_USERNAME_IS_USED.exception("Username is used");
        }
        if (userRepository.existsByEmail(scDf.toStripAndLowerCase(signUp.email()))) {
            throw WiwaException.USER_EMAIL_IS_USED.exception("Email is used");
        }
        if (!signUp.gdpr()) {
            throw WiwaException.GDPR.exception("GDPR has to be confirmed");
        }
        final UserDo userDo = userRepository.save(UserDo.builder()
                .username(scDf.toStripAndLowerCase(signUp.username()))
                .password(passwordEncoder.encode(signUp.password()))
                .titleBefore(signUp.titleBefore())
                .firstName(signUp.firstName())
                .midName(signUp.midName())
                .lastName(signUp.lastName())
                .titleAfter(signUp.titleAfter())
                .email(scDf.toStripAndLowerCase(signUp.email()))
                .gdpr(signUp.gdpr())
                .confirmed(false)
                .enabled(true)
                .build()
        );
        sendSignUpMail(userDo);
        return createAuthenticationResponse(userDo);
    }

    public AuthenticationResponseData refresh(final RefreshTokenData refreshToken) {
        final Map<String, String> data;
        try {
            data = verificationToken.parseToken(refreshToken.token());
        } catch (final JWTVerificationException jwtVerificationException) {
            throw new AccessDeniedException("Invalid token");
        }
        if (AuthToken.valueOf(data.get(AuthTokenKey.TYPE.name())) != AuthToken.REFRESH) {
            throw new AccessDeniedException("Invalid token");
        }
        final Long userId = Long.valueOf(data.get(AuthTokenKey.USER_ID.name()));
        final UserDo userDo = userRepository.findById(userId)
                .orElseThrow(() -> WiwaException.USER_NOT_FOUND.exception("User with id {0} not found", userId));
        return createAuthenticationResponse(userDo);
    }

    private AuthenticationResponseData createAuthenticationResponse(final UserDo user) {
        final Long issuedAt = System.currentTimeMillis();
        final Map<String, String> data = new HashMap<>();
        data.put(AuthTokenKey.TYPE.name(), AuthToken.REFRESH.name());
        data.put(AuthTokenKey.USER_ID.name(), user.getId().toString());
        final String token = verificationToken.generateToken(
                data,
                issuedAt,
                issuedAt + TimeUnit.MINUTES.toMillis(authConfigProperties.refreshTokenExpiration())
        );
        return new AuthenticationResponseData(jwtToken.generateToken(userUtilService.mapToUser(user), issuedAt), token);
    }

    private UserDo signUp(final Long userId) {
        final UserDo userDo = userUtilService.getUserDo(userId);
        userUtilService.checkEnabled(userDo);
        userDo.setConfirmed(true);
        final List<AuthorityDo> userAuthorities = authorityRepository.findByUserId(userId);
        if (userAuthorities.isEmpty()) {
            authorityRepository.saveUserAuthorities(userId, List.of(Authority.W_CUSTOMER));
        }
        return userRepository.save(userDo);
    }

    private UserDo resetPassword(final Long userId, final String newPassword) {
        final UserDo userDo = userUtilService.getUserDo(userId);
        userUtilService.checkEnabled(userDo);
        userDo.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(userDo);
    }

    private void sendResetPasswordMail(final UserDo user) {
        final Map<String, String> data = new HashMap<>();
        data.put(AuthTokenKey.TYPE.name(), AuthToken.RESET_PASSWORD.name());
        data.put(AuthTokenKey.USER_ID.name(), user.getId().toString());
        data.put(AuthTokenKey.NEW_PASSWORD.name(), randomString.alphaNumeric(2, 5, 3, 10, 10));
        final long issuedAt = System.currentTimeMillis();
        final String token = verificationToken.generateToken(
                data,
                issuedAt,
                issuedAt + TimeUnit.MINUTES.toMillis(authConfigProperties.resetPasswordTokenExpiration())
        );

        mailService.sendEmail(new MailData(
                appConfigProperties.mail(),
                null,
                List.of(user.getEmail()),
                applicationPropertyService.getProperty(WiwaProperty.AUTH_RESET_PASSWORD_MAIL_SUBJECT),
                MailTemplate.BASE,
                new MailContentData(
                        applicationPropertyService.getProperty(WiwaProperty.AUTH_RESET_PASSWORD_MAIL_TITLE),
                        Arrays.asList(
                                applicationPropertyService.getProperty(
                                        WiwaProperty.AUTH_RESET_PASSWORD_MAIL_MESSAGE
                                ),
                                MessageFormat.format(
                                        applicationPropertyService.getProperty(
                                                WiwaProperty.AUTH_RESET_PASSWORD_MAIL_PASSWORD_MESSAGE
                                        ),
                                        data.get(AuthTokenKey.NEW_PASSWORD.name())
                                )
                        ),
                        new MailLinkData(
                                getTokenUrl(appConfigProperties.webUrl(), appConfigProperties.confirmPath(), token),
                                applicationPropertyService.getProperty(
                                        WiwaProperty.AUTH_RESET_PASSWORD_MAIL_LINK
                                )
                        )
                ),
                null
        ));
    }

    private void sendSignUpMail(final UserDo user) {
        final Map<String, String> data = new HashMap<>();
        data.put(AuthTokenKey.TYPE.name(), AuthToken.SIGN_UP.name());
        data.put(AuthTokenKey.USER_ID.name(), user.getId().toString());
        final long issuedAt = System.currentTimeMillis();
        final String token = verificationToken.generateToken(
                data,
                issuedAt,
                issuedAt + TimeUnit.MINUTES.toMillis(authConfigProperties.signUpTokenExpiration())
        );

        mailService.sendEmail(new MailData(
                appConfigProperties.mail(),
                null,
                List.of(user.getEmail()),
                applicationPropertyService.getProperty(WiwaProperty.AUTH_SIGN_UP_MAIL_SUBJECT),
                MailTemplate.BASE,
                new MailContentData(
                        applicationPropertyService.getProperty(WiwaProperty.AUTH_SIGN_UP_MAIL_TITLE),
                        Collections.singletonList(
                                applicationPropertyService.getProperty(WiwaProperty.AUTH_SIGN_UP_MAIL_MESSAGE)
                        ),
                        new MailLinkData(
                                getTokenUrl(appConfigProperties.webUrl(), appConfigProperties.confirmPath(), token),
                                applicationPropertyService.getProperty(WiwaProperty.AUTH_SIGN_UP_MAIL_LINK)
                        )
                ),
                null
        ));
    }

    private String getTokenUrl(final String webUrl, final String path, final String token) {
        try {
            return webUrl + path + URLEncoder.encode(token, StandardCharsets.UTF_8);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
