package sk.janobono.wiwa.business.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import sk.janobono.wiwa.business.TestConfigProperties;
import sk.janobono.wiwa.business.TestMail;
import sk.janobono.wiwa.business.TestRepositories;
import sk.janobono.wiwa.business.TestUsers;
import sk.janobono.wiwa.business.impl.model.mail.MailData;
import sk.janobono.wiwa.business.impl.util.MailUtilService;
import sk.janobono.wiwa.business.impl.util.PropertyUtilService;
import sk.janobono.wiwa.business.impl.util.UserUtilService;
import sk.janobono.wiwa.business.model.auth.*;
import sk.janobono.wiwa.business.service.ApplicationPropertyService;
import sk.janobono.wiwa.business.service.AuthService;
import sk.janobono.wiwa.component.*;
import sk.janobono.wiwa.config.AuthConfigProperties;
import sk.janobono.wiwa.config.CommonConfigProperties;
import sk.janobono.wiwa.config.JwtConfigProperties;
import sk.janobono.wiwa.config.VerificationConfigProperties;
import sk.janobono.wiwa.dal.domain.UserDo;
import sk.janobono.wiwa.dal.repository.ApplicationPropertyRepository;
import sk.janobono.wiwa.dal.repository.AuthorityRepository;
import sk.janobono.wiwa.dal.repository.CodeListRepository;
import sk.janobono.wiwa.dal.repository.UserRepository;
import sk.janobono.wiwa.model.User;

import static org.assertj.core.api.Assertions.assertThat;

class AuthServiceTest {

    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private AuthorityRepository authorityRepository;
    private Captcha captcha;
    private UserUtilService userUtilService;
    private JwtToken jwtToken;
    private TestMail testMail;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        final AuthConfigProperties authConfigProperties = Mockito.mock(AuthConfigProperties.class);
        final CommonConfigProperties commonConfigProperties = Mockito.mock(CommonConfigProperties.class);
        final JwtConfigProperties jwtConfigProperties = Mockito.mock(JwtConfigProperties.class);
        final VerificationConfigProperties verificationConfigProperties = Mockito.mock(VerificationConfigProperties.class);
        final TestConfigProperties testConfigProperties = new TestConfigProperties();
        testConfigProperties.mock(authConfigProperties);
        testConfigProperties.mock(commonConfigProperties);
        testConfigProperties.mock(jwtConfigProperties);
        testConfigProperties.mock(verificationConfigProperties);

        final MailUtilService mailUtilService = Mockito.mock(MailUtilService.class);
        testMail = new TestMail();
        testMail.mock(mailUtilService);

        final ApplicationPropertyRepository applicationPropertyRepository = Mockito.mock(ApplicationPropertyRepository.class);
        authorityRepository = Mockito.mock(AuthorityRepository.class);
        final CodeListRepository codeListRepository = Mockito.mock(CodeListRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        final TestRepositories testRepositories = new TestRepositories();
        testRepositories.mock(applicationPropertyRepository);
        testRepositories.mock(authorityRepository);
        testRepositories.mock(codeListRepository);
        testRepositories.mock(userRepository);

        final ObjectMapper objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();

        final ApplicationPropertyService applicationPropertyService = new ApplicationPropertyServiceImpl(
                objectMapper, commonConfigProperties, jwtConfigProperties, new PropertyUtilService(applicationPropertyRepository), codeListRepository
        );

        passwordEncoder = new BCryptPasswordEncoder();
        final RandomString randomString = new RandomString();
        captcha = new Captcha(commonConfigProperties, randomString);
        jwtToken = new JwtToken(jwtConfigProperties);

        userUtilService = new UserUtilService(passwordEncoder, authorityRepository, userRepository);

        authService = new AuthServiceImpl(
                authConfigProperties,
                commonConfigProperties,
                passwordEncoder,
                captcha,
                jwtToken,
                randomString,
                new ScDf(),
                new VerificationToken(verificationConfigProperties),
                authorityRepository,
                userRepository,
                mailUtilService,
                userUtilService,
                applicationPropertyService
        );
    }

    @Test
    void signInAndRefresh_whenValidData_thenTheseResults() {
        new TestUsers().initUsers(passwordEncoder, userRepository, authorityRepository);
        AuthenticationResponseData response = authService.signIn(new SignInData(TestUsers.DEFAULT_ADMIN, TestUsers.PASSWORD));
        assertThat(response).isNotNull();
        response = authService.signIn(new SignInData(TestUsers.DEFAULT_MANAGER, TestUsers.PASSWORD));
        assertThat(response).isNotNull();
        response = authService.signIn(new SignInData(TestUsers.DEFAULT_EMPLOYEE, TestUsers.PASSWORD));
        assertThat(response).isNotNull();
        response = authService.signIn(new SignInData(TestUsers.DEFAULT_CUSTOMER, TestUsers.PASSWORD));
        assertThat(response).isNotNull();
        response = authService.refresh(new RefreshTokenData(response.refreshToken()));
        assertThat(response).isNotNull();
    }

    @Test
    void signUpAndConfirm_whenValidData_thenTheseResults() {
        AuthenticationResponseData response = authService.signUp(SignUpData.builder()
                .username("username")
                .password("password")
                .firstName("John")
                .lastName("Doe")
                .email("john_doe@test.sk")
                .gdpr(true)
                .captchaText("1234")
                .captchaToken(captcha.generateToken("1234"))
                .build());
        assertThat(response).isNotNull();
        final MailData mailData = testMail.getMail();
        assertThat(mailData).isNotNull();
        assertThat(mailData.content()).isNotNull();
        assertThat(mailData.content().mailLink()).isNotNull();
        assertThat(mailData.content().mailLink().href()).isNotBlank();
        final String confirmationToken = mailData.content().mailLink().href().replaceAll("http://localhost:8080/ui/confirm/", "");
        assertThat(confirmationToken).isNotBlank();
        response = authService.confirm(new ConfirmationData(confirmationToken));
        assertThat(response).isNotNull();
        final User user = jwtToken.parseToken(response.token());
        assertThat(user.username()).isEqualTo("username");
        assertThat(user.firstName()).isEqualTo("John");
        assertThat(user.lastName()).isEqualTo("Doe");
    }

    @Test
    void resendConfirmationAndConfirm_whenValidData_thenTheseResults() {
        new TestUsers().initUsers(passwordEncoder, userRepository, authorityRepository);
        final UserDo userDo = userUtilService.getUserDo(1L);
        authService.resendConfirmation(userUtilService.mapToUser(userDo), new ResendConfirmationData(
                "1234", captcha.generateToken("1234")
        ));
        final MailData mailData = testMail.getMail();
        assertThat(mailData).isNotNull();
        assertThat(mailData.content()).isNotNull();
        assertThat(mailData.content().mailLink()).isNotNull();
        assertThat(mailData.content().mailLink().href()).isNotBlank();
        final String confirmationToken = mailData.content().mailLink().href().replaceAll("http://localhost:8080/ui/confirm/", "");
        assertThat(confirmationToken).isNotBlank();
        final AuthenticationResponseData response = authService.confirm(new ConfirmationData(confirmationToken));
        assertThat(response).isNotNull();
    }

    @Test
    void resetPasswordAndConfirmAndSignIn_whenValidData_thenTheseResults() {
        new TestUsers().initUsers(passwordEncoder, userRepository, authorityRepository);
        final UserDo userDo = userUtilService.getUserDo(1L);
        authService.resetPassword(new ResetPasswordData(
                userDo.getEmail(), "1234", captcha.generateToken("1234")
        ));
        final MailData mailData = testMail.getMail();
        assertThat(mailData).isNotNull();
        assertThat(mailData.content()).isNotNull();
        assertThat(mailData.content().mailLink()).isNotNull();
        assertThat(mailData.content().mailLink().href()).isNotBlank();
        final String confirmationToken = mailData.content().mailLink().href().replaceAll("http://localhost:8080/ui/confirm/", "");
        assertThat(confirmationToken).isNotBlank();
        AuthenticationResponseData response = authService.confirm(new ConfirmationData(confirmationToken));
        assertThat(response).isNotNull();

        assertThat(mailData.content().lines()).hasSize(2);
        final String password = mailData.content().lines().getLast().replaceAll("New password: ", "");
        assertThat(password).isNotBlank();
        response = authService.signIn(new SignInData(userDo.getUsername(), password));
        assertThat(response).isNotNull();
    }

    @Test
    void change_whenValidData_thenTheseResults() {
        new TestUsers().initUsers(passwordEncoder, userRepository, authorityRepository);
        final UserDo userDo = userUtilService.getUserDo(1L);
        final User user = userUtilService.mapToUser(userDo);
        AuthenticationResponseData response = authService.changeEmail(user, ChangeEmailData.builder()
                .email("changed@test.com")
                .password(TestUsers.PASSWORD)
                .captchaText("1234")
                .captchaToken(captcha.generateToken("1234"))
                .build());
        assertThat(response).isNotNull();
        assertThat(jwtToken.parseToken(response.token()).email()).isEqualTo("changed@test.com");

        response = authService.changePassword(user, ChangePasswordData.builder()
                .oldPassword(TestUsers.PASSWORD)
                .newPassword("1234")
                .captchaText("1234")
                .captchaToken(captcha.generateToken("1234"))
                .build());
        assertThat(response).isNotNull();
        response = authService.signIn(new SignInData(user.username(), "1234"));
        assertThat(response).isNotNull();

        response = authService.changeUserDetails(user, ChangeUserDetailsData.builder()
                .titleBefore("titleBefore")
                .firstName("firstName")
                .midName("midName")
                .lastName("lastName")
                .titleAfter("titleAfter")
                .gdpr(true)
                .captchaText("1234")
                .captchaToken(captcha.generateToken("1234"))
                .build());
        assertThat(jwtToken.parseToken(response.token()).midName()).isEqualTo("midName");
    }
}
