package sk.janobono.wiwa.business.service;

import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sk.janobono.wiwa.business.model.auth.*;
import sk.janobono.wiwa.business.model.mail.MailContentSo;
import sk.janobono.wiwa.business.model.mail.MailLinkSo;
import sk.janobono.wiwa.business.model.mail.MailSo;
import sk.janobono.wiwa.business.model.mail.MailTemplate;
import sk.janobono.wiwa.component.Captcha;
import sk.janobono.wiwa.component.JwtToken;
import sk.janobono.wiwa.component.RandomString;
import sk.janobono.wiwa.component.VerificationToken;
import sk.janobono.wiwa.config.AuthConfigProperties;
import sk.janobono.wiwa.config.CommonConfigProperties;
import sk.janobono.wiwa.dal.domain.UserDo;
import sk.janobono.wiwa.dal.repository.AuthorityRepository;
import sk.janobono.wiwa.dal.repository.UserRepository;
import sk.janobono.wiwa.exception.WiwaException;
import sk.janobono.wiwa.mapper.UserMapper;
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
    private final MailService mailService;
    private final VerificationToken verificationToken;
    private final UserMapper userMapper;
    private final AuthorityRepository authorityRepository;
    private final UserRepository userRepository;
    private final ApplicationPropertyService applicationPropertyService;

    @Transactional
    public AuthenticationResponseSo confirm(final ConfirmationSo confirmation) {
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

    @Transactional
    public AuthenticationResponseSo changeEmail(final ChangeEmailSo changeEmail) {
        captcha.checkTokenValid(changeEmail.captchaText(), changeEmail.captchaToken());
        if (userRepository.existsByEmail(stripAndLowerCase(changeEmail.email()))) {
            throw WiwaException.USER_EMAIL_IS_USED.exception("Email is used");
        }
        final User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final UserDo userDo = getUser(principal.id());
        checkEnabled(userDo);
        checkPassword(userDo, changeEmail.password());
        userDo.setEmail(changeEmail.email());
        return createAuthenticationResponse(userRepository.save(userDo));
    }

    @Transactional
    public AuthenticationResponseSo changePassword(final ChangePasswordSo changePassword) {
        captcha.checkTokenValid(changePassword.captchaText(), changePassword.captchaToken());
        final User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final UserDo userDo = getUser(principal.id());
        checkEnabled(userDo);
        checkPassword(userDo, changePassword.oldPassword());
        userDo.setPassword(passwordEncoder.encode(changePassword.newPassword()));
        return createAuthenticationResponse(userRepository.save(userDo));
    }

    @Transactional
    public AuthenticationResponseSo changeUserDetails(final ChangeUserDetailsSo changeUserDetails) {
        captcha.checkTokenValid(changeUserDetails.captchaText(), changeUserDetails.captchaToken());
        final User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final UserDo userDo = getUser(principal.id());
        checkEnabled(userDo);
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

    public void resendConfirmation(final ResendConfirmationSo resendConfirmation) {
        captcha.checkTokenValid(resendConfirmation.captchaText(), resendConfirmation.captchaToken());
        final User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final UserDo userDo = userRepository.findById(principal.id()).orElseThrow(
                () -> WiwaException.USER_NOT_FOUND.exception("User with username {0} not found", principal.username())
        );
        sendSignUpMail(userDo);
    }

    public void resetPassword(final ResetPasswordSo resetPassword) {
        captcha.checkTokenValid(resetPassword.captchaText(), resetPassword.captchaToken());
        final UserDo userDo = userRepository.findByEmail(stripAndLowerCase(resetPassword.email())).orElseThrow(
                () -> WiwaException.USER_NOT_FOUND.exception("User with email {0} not found", resetPassword.email())
        );
        checkEnabled(userDo);
        sendResetPasswordMail(userDo);
    }

    public AuthenticationResponseSo signIn(final SignInSo signIn) {
        final UserDo userDo = userRepository.findByUsername(stripAndLowerCase(signIn.username())).orElseThrow(
                () -> WiwaException.USER_NOT_FOUND.exception("User with username {0} not found", signIn.username())
        );
        checkEnabled(userDo);
        checkPassword(userDo, signIn.password());
        return createAuthenticationResponse(userDo);
    }

    @Transactional
    public AuthenticationResponseSo signUp(final SignUpSo signUp) {
        captcha.checkTokenValid(signUp.captchaText(), signUp.captchaToken());
        if (userRepository.existsByUsername(stripAndLowerCase(signUp.username()))) {
            throw WiwaException.USER_USERNAME_IS_USED.exception("Username is used");
        }
        if (userRepository.existsByEmail(stripAndLowerCase(signUp.email()))) {
            throw WiwaException.USER_EMAIL_IS_USED.exception("Email is used");
        }
        if (!signUp.gdpr()) {
            throw WiwaException.GDPR.exception("GDPR has to be confirmed");
        }
        UserDo userDo = new UserDo();
        userDo.setUsername(stripAndLowerCase(signUp.username()));
        userDo.setPassword(passwordEncoder.encode(signUp.password()));
        userDo.setTitleBefore(signUp.titleBefore());
        userDo.setFirstName(signUp.firstName());
        userDo.setMidName(signUp.midName());
        userDo.setLastName(signUp.lastName());
        userDo.setTitleAfter(signUp.titleAfter());
        userDo.setEmail(stripAndLowerCase(signUp.email()));
        userDo.setGdpr(signUp.gdpr());
        userDo.setConfirmed(false);
        userDo.setEnabled(true);
        userDo = userRepository.save(userDo);
        sendSignUpMail(userDo);
        return createAuthenticationResponse(userDo);
    }

    public AuthenticationResponseSo refresh(final RefreshTokenSo refreshToken) {
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

    private UserDo getUser(final Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> WiwaException.USER_NOT_FOUND.exception("User with id {0} not found", id));
    }

    private void checkEnabled(final UserDo userDo) {
        if (!userDo.isEnabled()) {
            throw WiwaException.USER_IS_DISABLED.exception("User is disabled");
        }
    }

    private void checkPassword(final UserDo userDo, final String password) {
        if (!passwordEncoder.matches(password, userDo.getPassword())) {
            throw WiwaException.INVALID_CREDENTIALS.exception("Invalid credentials");
        }
    }

    private AuthenticationResponseSo createAuthenticationResponse(final UserDo user) {
        final Long issuedAt = System.currentTimeMillis();
        final Map<String, String> data = new HashMap<>();
        data.put(AuthTokenKey.TYPE.name(), AuthToken.REFRESH.name());
        data.put(AuthTokenKey.USER_ID.name(), user.getId().toString());
        final String token = verificationToken.generateToken(
                data,
                issuedAt,
                issuedAt + TimeUnit.HOURS.toMillis(authConfigProperties.refreshTokenExpiration())
        );
        return new AuthenticationResponseSo(jwtToken.generateToken(userMapper.mapToUser(user), issuedAt), token);
    }

    private UserDo signUp(final Long userId) {
        final UserDo userDo = getUser(userId);
        checkEnabled(userDo);
        userDo.setConfirmed(true);
        if (userDo.getAuthorities().size() == 0) {
            userDo.getAuthorities().add(authorityRepository.findByAuthority(Authority.W_CUSTOMER)
                    .orElseThrow(() -> WiwaException.AUTHORITY_NOT_FOUND.exception(Authority.W_CUSTOMER.name())));
        }
        return userRepository.save(userDo);
    }

    private UserDo resetPassword(final Long userId, final String newPassword) {
        final UserDo userDo = getUser(userId);
        checkEnabled(userDo);
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
                issuedAt + TimeUnit.HOURS.toMillis(authConfigProperties.resetPasswordTokenExpiration())
        );

        mailService.sendEmail(new MailSo(
                appConfigProperties.mail(),
                null,
                List.of(user.getEmail()),
                applicationPropertyService.getProperty(WiwaProperty.AUTH_RESET_PASSWORD_MAIL_SUBJECT),
                MailTemplate.BASE,
                new MailContentSo(
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
                        new MailLinkSo(
                                getTokenUrl(appConfigProperties.webUrl(), token),
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
                issuedAt + TimeUnit.HOURS.toMillis(authConfigProperties.signUpTokenExpiration())
        );

        mailService.sendEmail(new MailSo(
                appConfigProperties.mail(),
                null,
                List.of(user.getEmail()),
                applicationPropertyService.getProperty(WiwaProperty.AUTH_SIGN_UP_MAIL_SUBJECT),
                MailTemplate.BASE,
                new MailContentSo(
                        applicationPropertyService.getProperty(WiwaProperty.AUTH_SIGN_UP_MAIL_TITLE),
                        Collections.singletonList(
                                applicationPropertyService.getProperty(WiwaProperty.AUTH_SIGN_UP_MAIL_MESSAGE)
                        ),
                        new MailLinkSo(
                                getTokenUrl(appConfigProperties.webUrl(), token),
                                applicationPropertyService.getProperty(WiwaProperty.AUTH_SIGN_UP_MAIL_LINK)
                        )
                ),
                null
        ));
    }

    private String getTokenUrl(final String webUrl, final String token) {
        try {
            return webUrl + "/auth/confirm/" + URLEncoder.encode(token, StandardCharsets.UTF_8);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String stripAndLowerCase(final String s) {
        if (!Optional.ofNullable(s).map(String::isBlank).orElse(true)) {
            return s.strip().toLowerCase();
        }
        return s;
    }
}
