package sk.janobono.wiwa.business.impl.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import sk.janobono.wiwa.business.TestRepositories;
import sk.janobono.wiwa.business.TestUsers;
import sk.janobono.wiwa.dal.domain.UserDo;
import sk.janobono.wiwa.dal.repository.AuthorityRepository;
import sk.janobono.wiwa.dal.repository.UserRepository;
import sk.janobono.wiwa.exception.ApplicationException;
import sk.janobono.wiwa.exception.WiwaException;
import sk.janobono.wiwa.model.User;

import static org.assertj.core.api.Assertions.assertThat;

class UserUtilServiceTest {

    private PasswordEncoder passwordEncoder;
    private UserUtilService userUtilService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();

        final AuthorityRepository authorityRepository = Mockito.mock(AuthorityRepository.class);
        final UserRepository userRepository = Mockito.mock(UserRepository.class);
        final TestRepositories testRepositories = new TestRepositories();
        testRepositories.mock(authorityRepository);
        testRepositories.mock(userRepository);
        new TestUsers().initUsers(passwordEncoder, userRepository, authorityRepository);

        userUtilService = new UserUtilService(passwordEncoder, authorityRepository, userRepository);
    }

    @Test
    void fullTest() {
        ApplicationException applicationException = Assertions.assertThrows(ApplicationException.class,
                () -> userUtilService.checkEnabled(UserDo.builder().enabled(false).build()));
        assertThat(applicationException.getCode()).isEqualTo(WiwaException.USER_IS_DISABLED.name());

        userUtilService.checkEnabled(UserDo.builder().enabled(true).build());

        applicationException = Assertions.assertThrows(ApplicationException.class,
                () -> userUtilService.checkPassword(UserDo.builder()
                                .password(passwordEncoder.encode("test1"))
                                .build(),
                        passwordEncoder.encode("test2")));
        assertThat(applicationException.getCode()).isEqualTo(WiwaException.INVALID_CREDENTIALS.name());

        userUtilService.checkPassword(UserDo.builder().password(passwordEncoder.encode("test")).build(), "test");

        applicationException = Assertions.assertThrows(ApplicationException.class, () -> userUtilService.getUserDo(-1L));
        assertThat(applicationException.getCode()).isEqualTo(WiwaException.USER_NOT_FOUND.name());

        final UserDo userDo = userUtilService.getUserDo(1L);
        final User user = userUtilService.mapToUser(userDo);
        assertThat(user.authorities()).hasSize(1);
    }
}
