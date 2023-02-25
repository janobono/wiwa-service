package sk.janobono.wiwa.dal.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import sk.janobono.wiwa.common.model.Authority;
import sk.janobono.wiwa.dal.domain.UserDo;
import sk.janobono.wiwa.dal.model.UserProfileDo;
import sk.janobono.wiwa.dal.model.UserSearchCriteriaDo;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(
        properties = {
                "logging.level.sk.r3n.jdbc=debug",
                "spring.datasource.username=app",
                "spring.datasource.password=app",
                "spring.flyway.clean-disabled=false"
        }
)
class UserRepositoryImplIT {

    private static final UserDo TEST_USER_1 = new UserDo(
            1L,
            "username1",
            "password1",
            "titleBefore1",
            "firstName1",
            "midName1",
            "lastName1",
            "titleAfter1",
            "email1",
            true,
            true,
            true,
            Arrays.stream(Authority.values()).collect(Collectors.toSet())
    );

    private static final UserDo TEST_USER_2 = new UserDo(
            2L,
            "username2",
            "password2",
            null,
            "firstName2",
            null,
            "lastName2",
            null,
            "email2",
            false,
            false,
            false,
            Set.of()
    );

    @Container
    public static PostgreSQLContainer<?> postgresDB = new PostgreSQLContainer<>
            ("postgres:15-alpine")
            .withDatabaseName("app")
            .withUsername("app")
            .withPassword("app");

    @DynamicPropertySource
    public static void properties(DynamicPropertyRegistry registry) throws Exception {
        registry.add("spring.datasource.url", postgresDB::getJdbcUrl);
    }

    @Autowired
    public AuthorityRepository authorityRepository;

    @Autowired
    public UserRepository userRepository;

    @Test
    void fullTest() {
        assertThat(authorityRepository).isNotNull();
        assertThat(userRepository).isNotNull();

        for (Authority authority : Authority.values()) {
            authorityRepository.addAuthority(authority);
        }
        assertThat(authorityRepository.count()).isEqualTo(Authority.values().length);

        assertThat(userRepository.count()).isEqualTo(0L);

        assertThat(userRepository.exists(TEST_USER_1.id())).isFalse();
        assertThat(userRepository.existsByEmail(TEST_USER_1.email())).isFalse();
        assertThat(userRepository.existsByUsername(TEST_USER_1.username())).isFalse();

        assertThat(userRepository.exists(TEST_USER_2.id())).isFalse();
        assertThat(userRepository.existsByEmail(TEST_USER_2.email())).isFalse();
        assertThat(userRepository.existsByUsername(TEST_USER_2.username())).isFalse();

        UserDo userDo = userRepository.addUser(TEST_USER_1);
        assertThat(userDo).usingRecursiveComparison().isEqualTo(TEST_USER_1);

        assertThat(userRepository.count()).isEqualTo(1L);

        assertThat(userRepository.exists(TEST_USER_1.id())).isTrue();
        assertThat(userRepository.existsByEmail(TEST_USER_1.email())).isTrue();
        assertThat(userRepository.existsByUsername(TEST_USER_1.username())).isTrue();

        assertThat(userRepository.getUser(TEST_USER_1.id())).isEqualTo(Optional.of(TEST_USER_1));
        assertThat(userRepository.getUserByEmail(TEST_USER_1.email())).isEqualTo(Optional.of(TEST_USER_1));
        assertThat(userRepository.getUserByUsername(TEST_USER_1.username())).isEqualTo(Optional.of(TEST_USER_1));
        assertThat(userRepository.getUserEnabled(TEST_USER_1.id())).isEqualTo(Optional.of(TEST_USER_1.enabled()));

        userDo = userRepository.addUser(TEST_USER_2);
        assertThat(userDo).usingRecursiveComparison().isEqualTo(TEST_USER_2);

        assertThat(userRepository.count()).isEqualTo(2L);

        assertThat(userRepository.exists(TEST_USER_2.id())).isTrue();
        assertThat(userRepository.existsByEmail(TEST_USER_2.email())).isTrue();
        assertThat(userRepository.existsByUsername(TEST_USER_2.username())).isTrue();

        assertThat(userRepository.getUser(TEST_USER_2.id())).isEqualTo(Optional.of(TEST_USER_2));
        assertThat(userRepository.getUserByEmail(TEST_USER_2.email())).isEqualTo(Optional.of(TEST_USER_2));
        assertThat(userRepository.getUserByUsername(TEST_USER_2.username())).isEqualTo(Optional.of(TEST_USER_2));
        assertThat(userRepository.getUserEnabled(TEST_USER_2.id())).isEqualTo(Optional.of(TEST_USER_2.enabled()));

        Page<UserDo> page = userRepository.getUsers(Pageable.unpaged());
        assertThat(page.getTotalElements()).isEqualTo(2L);
        assertThat(page.getContent().get(0).username()).isEqualTo(TEST_USER_1.username());
        assertThat(page.getContent().get(1).username()).isEqualTo(TEST_USER_2.username());

        page = userRepository.getUsers(PageRequest.of(0, 5, Sort.Direction.DESC, "username"));
        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent().get(0).username()).isEqualTo(TEST_USER_2.username());
        assertThat(page.getContent().get(1).username()).isEqualTo(TEST_USER_1.username());

        page = userRepository.getUsers(new UserSearchCriteriaDo("firstname1", null, null), Pageable.unpaged());
        assertThat(page.getTotalElements()).isEqualTo(1L);
        assertThat(page.getContent().get(0).username()).isEqualTo(TEST_USER_1.username());

        page = userRepository.getUsers(new UserSearchCriteriaDo(null, "username1", null), Pageable.unpaged());
        assertThat(page.getTotalElements()).isEqualTo(1L);
        assertThat(page.getContent().get(0).username()).isEqualTo(TEST_USER_1.username());

        page = userRepository.getUsers(new UserSearchCriteriaDo(null, null, "email1"), Pageable.unpaged());
        assertThat(page.getTotalElements()).isEqualTo(1L);
        assertThat(page.getContent().get(0).username()).isEqualTo(TEST_USER_1.username());

        userDo = userRepository.setUserAuthorities(TEST_USER_2.id(), TEST_USER_1.authorities());
        assertThat(userDo.authorities()).isEqualTo(TEST_USER_1.authorities());

        userDo = userRepository.setUserConfirmed(TEST_USER_2.id(), TEST_USER_1.confirmed());
        assertThat(userDo.confirmed()).isEqualTo(TEST_USER_1.confirmed());

        userDo = userRepository.setUserConfirmedAndAuthorities(TEST_USER_2.id(), TEST_USER_2.confirmed(), TEST_USER_2.authorities());
        assertThat(userDo).usingRecursiveComparison().isEqualTo(TEST_USER_2);

        userDo = userRepository.setUserEmail(TEST_USER_2.id(), "xyz");
        assertThat(userDo.email()).isEqualTo("xyz");

        userDo = userRepository.setUserEnabled(TEST_USER_2.id(), TEST_USER_1.enabled());
        assertThat(userDo.enabled()).isEqualTo(TEST_USER_1.enabled());

        userDo = userRepository.setUserPassword(TEST_USER_2.id(), TEST_USER_1.password());
        assertThat(userDo.password()).isEqualTo(TEST_USER_1.password());

        userDo = userRepository.setUserProfile(TEST_USER_2.id(), new UserProfileDo(
                TEST_USER_1.titleBefore(),
                TEST_USER_1.firstName(),
                TEST_USER_1.midName(),
                TEST_USER_1.lastName(),
                TEST_USER_1.titleAfter()
        ));
        assertThat(userDo.titleBefore()).isEqualTo(TEST_USER_1.titleBefore());
        assertThat(userDo.firstName()).isEqualTo(TEST_USER_1.firstName());
        assertThat(userDo.midName()).isEqualTo(TEST_USER_1.midName());
        assertThat(userDo.lastName()).isEqualTo(TEST_USER_1.lastName());
        assertThat(userDo.titleAfter()).isEqualTo(TEST_USER_1.titleAfter());

        userDo = userRepository.setUserProfileAndGdpr(TEST_USER_2.id(), new UserProfileDo(
                TEST_USER_2.titleBefore(),
                TEST_USER_2.firstName(),
                TEST_USER_2.midName(),
                TEST_USER_2.lastName(),
                TEST_USER_2.titleAfter()
        ), TEST_USER_1.gdpr());
        assertThat(userDo.titleBefore()).isEqualTo(TEST_USER_2.titleBefore());
        assertThat(userDo.firstName()).isEqualTo(TEST_USER_2.firstName());
        assertThat(userDo.midName()).isEqualTo(TEST_USER_2.midName());
        assertThat(userDo.lastName()).isEqualTo(TEST_USER_2.lastName());
        assertThat(userDo.titleAfter()).isEqualTo(TEST_USER_2.titleAfter());
        assertThat(userDo.gdpr()).isEqualTo(TEST_USER_1.gdpr());

        userDo = userRepository.setUser(TEST_USER_1);
        assertThat(userDo).usingRecursiveComparison().isEqualTo(TEST_USER_1);

        userDo = userRepository.setUser(TEST_USER_2);
        assertThat(userDo).usingRecursiveComparison().isEqualTo(TEST_USER_2);

        userRepository.deleteUser(TEST_USER_1.id());
        userRepository.deleteUser(TEST_USER_2.id());
        assertThat(userRepository.count()).isEqualTo(0L);
    }
}
