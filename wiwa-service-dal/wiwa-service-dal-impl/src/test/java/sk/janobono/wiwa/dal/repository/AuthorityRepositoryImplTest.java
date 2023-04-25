package sk.janobono.wiwa.dal.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import sk.janobono.wiwa.common.model.Authority;

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
class AuthorityRepositoryImplTest {

    @Container
    public static PostgreSQLContainer<?> postgresDB = new PostgreSQLContainer<>
            ("postgres:15-alpine")
            .withDatabaseName("app")
            .withUsername("app")
            .withPassword("app");

    @DynamicPropertySource
    public static void properties(final DynamicPropertyRegistry registry) throws Exception {
        registry.add("spring.datasource.url", postgresDB::getJdbcUrl);
    }

    @Autowired
    public AuthorityRepository authorityRepository;

    @Test
    void fullTest() {
        assertThat(authorityRepository).isNotNull();

        assertThat(authorityRepository.count()).isEqualTo(0L);

        authorityRepository.addAuthority(Authority.W_ADMIN);
        assertThat(authorityRepository.count()).isEqualTo(1L);

        authorityRepository.addAuthority(Authority.W_MANAGER);
        assertThat(authorityRepository.count()).isEqualTo(2L);

        authorityRepository.addAuthority(Authority.W_EMPLOYEE);
        assertThat(authorityRepository.count()).isEqualTo(3L);

        authorityRepository.addAuthority(Authority.W_CUSTOMER);
        assertThat(authorityRepository.count()).isEqualTo(4L);
    }
}
