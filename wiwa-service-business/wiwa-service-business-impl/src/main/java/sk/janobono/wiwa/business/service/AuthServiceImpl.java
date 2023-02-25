package sk.janobono.wiwa.business.service;

import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import sk.janobono.wiwa.business.config.AuthConfigProperties;
import sk.janobono.wiwa.business.mapper.UserMapper;
import sk.janobono.wiwa.business.model.auth.*;
import sk.janobono.wiwa.business.model.mail.MailContentSo;
import sk.janobono.wiwa.business.model.mail.MailLinkSo;
import sk.janobono.wiwa.business.model.mail.MailSo;
import sk.janobono.wiwa.business.model.mail.MailTemplate;
import sk.janobono.wiwa.common.component.Captcha;
import sk.janobono.wiwa.common.component.JwtToken;
import sk.janobono.wiwa.common.component.RandomString;
import sk.janobono.wiwa.common.component.VerificationToken;
import sk.janobono.wiwa.common.config.CommonConfigProperties;
import sk.janobono.wiwa.common.exception.WiwaException;
import sk.janobono.wiwa.common.model.Authority;
import sk.janobono.wiwa.common.model.UserSo;
import sk.janobono.wiwa.common.model.WiwaProperty;
import sk.janobono.wiwa.dal.domain.UserDo;
import sk.janobono.wiwa.dal.model.UserProfileDo;
import sk.janobono.wiwa.dal.repository.UserRepository;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final CommonConfigProperties appConfigProperties;
    private final AuthConfigProperties authConfigProperties;
    private final PasswordEncoder passwordEncoder;
    private final Captcha captcha;
    private final JwtToken jwtToken;
    private final RandomString randomString;
    private final MailService mailService;
    private final VerificationToken verificationToken;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final ApplicationPropertyService applicationPropertyService;

    public AuthenticationResponseSo confirm(ConfirmationSo confirmationSo) {
        log.debug("confirm({})", confirmationSo);
        Map<String, String> data = verificationToken.parseToken(confirmationSo.token());
        UserDo userDo = switch (AuthToken.valueOf(data.get(AuthTokenKey.TYPE.name()))) {
            case SIGN_UP -> signUp(
                    Long.valueOf(data.get(AuthTokenKey.USER_ID.name()))
            );
            case RESET_PASSWORD -> resetPassword(
                    Long.valueOf(data.get(AuthTokenKey.USER_ID.name())),
                    data.get(AuthTokenKey.NEW_PASSWORD.name())
            );
            default -> throw WiwaException.UNSUPPORTED_VALIDATION_TOKEN.exception("Unsupported validation token");
        };
        AuthenticationResponseSo authenticationResponse = createAuthenticationResponse(userDo);
        log.info("confirm({}) - {}", confirmationSo, authenticationResponse);
        return authenticationResponse;
    }

    public AuthenticationResponseSo changeEmail(ChangeEmailSo changeEmailSo) {
        log.debug("changeEmail({})", changeEmailSo);
        captcha.checkTokenValid(changeEmailSo.captchaText(), changeEmailSo.captchaToken());
        if (userRepository.existsByEmail(stripAndLowerCase(changeEmailSo.email()))) {
            throw WiwaException.USER_EMAIL_IS_USED.exception("Email is used");
        }
        UserSo userSo = (UserSo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        checkExists(userSo.id());
        checkEnabled(userSo.id());
        checkPassword(userSo.id(), changeEmailSo.password());
        UserDo userDo = userRepository.setUserEmail(userSo.id(), changeEmailSo.email());
        AuthenticationResponseSo authenticationResponse = createAuthenticationResponse(userDo);
        log.info("changeEmail({}) - {}", changeEmailSo, authenticationResponse);
        return authenticationResponse;
    }

    public AuthenticationResponseSo changePassword(ChangePasswordSo changePasswordSo) {
        log.debug("changePassword({})", changePasswordSo);
        captcha.checkTokenValid(changePasswordSo.captchaText(), changePasswordSo.captchaToken());
        UserSo userSo = (UserSo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        checkExists(userSo.id());
        checkEnabled(userSo.id());
        checkPassword(userSo.id(), changePasswordSo.oldPassword());
        UserDo userDo = userRepository.setUserPassword(
                userSo.id(),
                passwordEncoder.encode(changePasswordSo.newPassword())
        );
        AuthenticationResponseSo authenticationResponse = createAuthenticationResponse(userDo);
        log.info("changePassword({}) - {}", changePasswordSo, authenticationResponse);
        return authenticationResponse;
    }

    public AuthenticationResponseSo changeUserDetails(ChangeUserDetailsSo changeUserDetailsSo) {
        log.debug("changeUserDetails({})", changeUserDetailsSo);
        captcha.checkTokenValid(changeUserDetailsSo.captchaText(), changeUserDetailsSo.captchaToken());
        UserSo userSo = (UserSo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        checkExists(userSo.id());
        checkEnabled(userSo.id());
        if (!changeUserDetailsSo.gdpr()) {
            throw WiwaException.GDPR.exception("GDPR has to be confirmed");
        }
        UserDo userDo = userRepository.setUserProfileAndGdpr(userSo.id(), new UserProfileDo(
                changeUserDetailsSo.titleBefore(),
                changeUserDetailsSo.firstName(),
                changeUserDetailsSo.midName(),
                changeUserDetailsSo.lastName(),
                changeUserDetailsSo.titleAfter()
        ), changeUserDetailsSo.gdpr());
        AuthenticationResponseSo authenticationResponse = createAuthenticationResponse(userDo);
        log.info("changeUserDetails({}) - {}", changeUserDetailsSo, authenticationResponse);
        return authenticationResponse;
    }

    public void resendConfirmation(ResendConfirmationSo resendConfirmationRequestDto) {
        log.debug("resendConfirmation({})", resendConfirmationRequestDto);
        captcha.checkTokenValid(resendConfirmationRequestDto.captchaText(), resendConfirmationRequestDto.captchaToken());
        UserSo userSO = (UserSo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDo userDo = userRepository.getUser(userSO.id()).orElseThrow(
                () -> WiwaException.USER_NOT_FOUND.exception("User with username {0} not found", userSO.username())
        );
        sendSignUpMail(userDo);
    }

    public void resetPassword(ResetPasswordSo resetPasswordRequestDto) {
        log.debug("resetPassword({})", resetPasswordRequestDto);
        captcha.checkTokenValid(resetPasswordRequestDto.captchaText(), resetPasswordRequestDto.captchaToken());
        UserDo userDo = userRepository.getUserByEmail(stripAndLowerCase(resetPasswordRequestDto.email())).orElseThrow(
                () -> WiwaException.USER_NOT_FOUND.exception("User with email {0} not found", resetPasswordRequestDto.email())
        );
        if (!userDo.enabled()) {
            throw WiwaException.USER_IS_DISABLED.exception("User is disabled");
        }
        sendResetPasswordMail(resetPasswordRequestDto, userDo);
    }

    public AuthenticationResponseSo signIn(SignInSo signInRequestDto) {
        log.debug("signIn({})", signInRequestDto);
        UserDo userDo = userRepository.getUserByUsername(stripAndLowerCase(signInRequestDto.username())).orElseThrow(
                () -> WiwaException.USER_NOT_FOUND.exception("User with username {0} not found", signInRequestDto.username())
        );
        if (!userDo.enabled()) {
            throw WiwaException.USER_IS_DISABLED.exception("User is disabled");
        }
        if (!passwordEncoder.matches(signInRequestDto.password(), userDo.password())) {
            throw WiwaException.INVALID_CREDENTIALS.exception("Invalid credentials");
        }
        AuthenticationResponseSo authenticationResponse = createAuthenticationResponse(userDo);
        log.info("authenticate({}) - {}", signInRequestDto, authenticationResponse);
        return authenticationResponse;
    }

    public AuthenticationResponseSo signUp(SignUpSo signUpRequestDto) {
        log.debug("signUp({})", signUpRequestDto);
        captcha.checkTokenValid(signUpRequestDto.captchaText(), signUpRequestDto.captchaToken());
        if (userRepository.existsByUsername(stripAndLowerCase(signUpRequestDto.username()))) {
            throw WiwaException.USER_USERNAME_IS_USED.exception("Username is used");
        }
        if (userRepository.existsByEmail(stripAndLowerCase(signUpRequestDto.email()))) {
            throw WiwaException.USER_EMAIL_IS_USED.exception("Email is used");
        }
        if (!signUpRequestDto.gdpr()) {
            throw WiwaException.GDPR.exception("GDPR has to be confirmed");
        }
        UserDo userDo = new UserDo(
                null,
                stripAndLowerCase(signUpRequestDto.username()),
                passwordEncoder.encode(signUpRequestDto.password()),
                signUpRequestDto.titleBefore(),
                signUpRequestDto.firstName(),
                signUpRequestDto.midName(),
                signUpRequestDto.lastName(),
                signUpRequestDto.titleAfter(),
                stripAndLowerCase(signUpRequestDto.email()),
                signUpRequestDto.gdpr(),
                false,
                true,
                new HashSet<>()
        );
        userDo = userRepository.addUser(userDo);
        sendSignUpMail(userDo);
        AuthenticationResponseSo authenticationResponse = createAuthenticationResponse(userDo);
        log.info("signUp({}) - {}", signUpRequestDto, authenticationResponse);
        return authenticationResponse;
    }

    public AuthenticationResponseSo refresh(RefreshTokenSo refreshTokenDto) {
        log.debug("refresh({})", refreshTokenDto);
        Map<String, String> data;
        try {
            data = verificationToken.parseToken(refreshTokenDto.token());
        } catch (JWTVerificationException jwtVerificationException) {
            throw new AccessDeniedException("Invalid token");
        }
        if (AuthToken.valueOf(data.get(AuthTokenKey.TYPE.name())) != AuthToken.REFRESH) {
            throw new AccessDeniedException("Invalid token");
        }
        Long userId = Long.valueOf(data.get(AuthTokenKey.USER_ID.name()));
        UserDo userDo = userRepository.getUser(userId).orElseThrow(
                () -> WiwaException.USER_NOT_FOUND.exception("User with id {0} not found", userId)
        );
        AuthenticationResponseSo authenticationResponse = createAuthenticationResponse(userDo);
        log.info("refresh({}) - {}", refreshTokenDto, authenticationResponse);
        return authenticationResponse;
    }

    private AuthenticationResponseSo createAuthenticationResponse(UserDo userDo) {
        Long issuedAt = System.currentTimeMillis();
        Map<String, String> data = new HashMap<>();
        data.put(AuthTokenKey.TYPE.name(), AuthToken.REFRESH.name());
        data.put(AuthTokenKey.USER_ID.name(), userDo.id().toString());
        String token = verificationToken.generateToken(
                data,
                issuedAt,
                issuedAt + TimeUnit.HOURS.toMillis(authConfigProperties.refreshTokenExpiration())
        );
        return new AuthenticationResponseSo(jwtToken.generateToken(userMapper.mapToSo(userDo), issuedAt), token);
    }

    private UserDo signUp(Long userId) {
        log.debug("signUp({})", userId);
        checkExists(userId);
        checkEnabled(userId);
        return userRepository.setUserConfirmedAndAuthorities(userId, true, Set.of(Authority.W_CUSTOMER));
    }

    private UserDo resetPassword(Long userId, String newPassword) {
        log.debug("resetPassword({})", userId);
        checkExists(userId);
        checkEnabled(userId);
        return userRepository.setUserPassword(userId, passwordEncoder.encode(newPassword));
    }

    private void checkExists(Long id) {
        if (!userRepository.exists(id)) {
            throw WiwaException.USER_NOT_FOUND.exception("User with id {0} not found", id);
        }
    }

    private void checkEnabled(Long id) {
        log.debug("checkEnabled({})", id);
        if (!userRepository.getUserEnabled(id).orElse(false)) {
            throw WiwaException.USER_IS_DISABLED.exception("User is disabled");
        }
    }

    private void checkPassword(Long id, String password) {
        log.debug("checkPassword({})", id);
        if (!passwordEncoder.matches(password,
                userRepository.getUserPassword(id).orElseThrow(
                        () -> WiwaException.INVALID_CREDENTIALS.exception("Invalid credentials")
                ))) {
            throw WiwaException.INVALID_CREDENTIALS.exception("Invalid credentials");
        }
    }

    private void sendResetPasswordMail(ResetPasswordSo resetPasswordSo, UserDo userDo) {
        log.debug("sendResetPasswordMail({},{})", resetPasswordSo, userDo);

        Map<String, String> data = new HashMap<>();
        data.put(AuthTokenKey.TYPE.name(), AuthToken.RESET_PASSWORD.name());
        data.put(AuthTokenKey.USER_ID.name(), userDo.id().toString());
        data.put(AuthTokenKey.NEW_PASSWORD.name(), randomString.alphaNumeric(2, 5, 3, 10, 10));
        long issuedAt = System.currentTimeMillis();
        String token = verificationToken.generateToken(
                data,
                issuedAt,
                issuedAt + TimeUnit.HOURS.toMillis(authConfigProperties.resetPasswordTokenExpiration())
        );

        mailService.sendEmail(new MailSo(
                appConfigProperties.mail(),
                null,
                List.of(userDo.email()),
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

    private void sendSignUpMail(UserDo userDo) {
        log.debug("sendSignUpMail({})", userDo);

        Map<String, String> data = new HashMap<>();
        data.put(AuthTokenKey.TYPE.name(), AuthToken.SIGN_UP.name());
        data.put(AuthTokenKey.USER_ID.name(), userDo.id().toString());
        long issuedAt = System.currentTimeMillis();
        String token = verificationToken.generateToken(
                data,
                issuedAt,
                issuedAt + TimeUnit.HOURS.toMillis(authConfigProperties.signUpTokenExpiration())
        );

        mailService.sendEmail(new MailSo(
                appConfigProperties.mail(),
                null,
                List.of(userDo.email()),
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

    private String getTokenUrl(String webUrl, String token) {
        try {
            return webUrl + "/auth/confirm/" + URLEncoder.encode(token, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String stripAndLowerCase(String s) {
        if (StringUtils.hasLength(s)) {
            return s.strip().toLowerCase();
        }
        return s;
    }
}
