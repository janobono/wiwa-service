package sk.janobono.wiwa.dal.repository;

import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import sk.janobono.wiwa.dal.domain.ApplicationPropertyDo;

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
class ApplicationPropertyRepositoryImplIT {

    private static final ApplicationPropertyDo TEST_APPLICATION_PROPERTY_1 = new ApplicationPropertyDo("group", "key", "", "value1");
    private static final ApplicationPropertyDo TEST_APPLICATION_PROPERTY_2 = new ApplicationPropertyDo("group", "key", "language", "value2");

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
    public ApplicationPropertyRepository applicationPropertyRepository;

    @Test
    void fullTest() {
        assertThat(applicationPropertyRepository).isNotNull();

        assertThat(applicationPropertyRepository.count()).isEqualTo(0L);
        assertThat(applicationPropertyRepository.exists(
                TEST_APPLICATION_PROPERTY_1.group(),
                TEST_APPLICATION_PROPERTY_1.key(),
                TEST_APPLICATION_PROPERTY_1.language())
        ).isFalse();
        assertThat(applicationPropertyRepository.exists(
                TEST_APPLICATION_PROPERTY_2.group(),
                TEST_APPLICATION_PROPERTY_2.key(),
                TEST_APPLICATION_PROPERTY_2.language())
        ).isFalse();

        ApplicationPropertyDo applicationPropertyDo = applicationPropertyRepository.addApplicationProperty(
                TEST_APPLICATION_PROPERTY_1
        );
        assertThat(applicationPropertyDo).usingRecursiveComparison().isEqualTo(TEST_APPLICATION_PROPERTY_1);

        applicationPropertyDo = applicationPropertyRepository.addApplicationProperty(
                TEST_APPLICATION_PROPERTY_2
        );
        assertThat(applicationPropertyDo).usingRecursiveComparison().isEqualTo(TEST_APPLICATION_PROPERTY_2);

        assertThat(applicationPropertyRepository.count()).isEqualTo(2L);
        assertThat(applicationPropertyRepository.exists(
                TEST_APPLICATION_PROPERTY_1.group(),
                TEST_APPLICATION_PROPERTY_1.key(),
                TEST_APPLICATION_PROPERTY_1.language())
        ).isTrue();
        assertThat(applicationPropertyRepository.exists(
                TEST_APPLICATION_PROPERTY_2.group(),
                TEST_APPLICATION_PROPERTY_2.key(),
                TEST_APPLICATION_PROPERTY_2.language())
        ).isTrue();

        applicationPropertyDo = applicationPropertyRepository.getApplicationProperty(
                TEST_APPLICATION_PROPERTY_1.group(),
                TEST_APPLICATION_PROPERTY_1.key(),
                TEST_APPLICATION_PROPERTY_1.language()).orElseThrow();
        assertThat(applicationPropertyDo).usingRecursiveComparison().isEqualTo(TEST_APPLICATION_PROPERTY_1);

        applicationPropertyDo = applicationPropertyRepository.getApplicationProperty(
                TEST_APPLICATION_PROPERTY_2.group(),
                TEST_APPLICATION_PROPERTY_2.key(),
                TEST_APPLICATION_PROPERTY_2.language()).orElseThrow();
        assertThat(applicationPropertyDo).usingRecursiveComparison().isEqualTo(TEST_APPLICATION_PROPERTY_2);

        applicationPropertyDo = applicationPropertyRepository.setApplicationProperty(new ApplicationPropertyDo(
                TEST_APPLICATION_PROPERTY_1.group(),
                TEST_APPLICATION_PROPERTY_1.key(),
                TEST_APPLICATION_PROPERTY_1.language(),
                "testc"
        ));
        assertThat(applicationPropertyDo)
                .usingRecursiveComparison(RecursiveComparisonConfiguration.builder().withIgnoredFields("value").build())
                .isEqualTo(TEST_APPLICATION_PROPERTY_1);
        assertThat(applicationPropertyDo.value()).isEqualTo("testc");

        applicationPropertyDo = applicationPropertyRepository.setApplicationProperty(new ApplicationPropertyDo(
                TEST_APPLICATION_PROPERTY_2.group(),
                TEST_APPLICATION_PROPERTY_2.key(),
                TEST_APPLICATION_PROPERTY_2.language(),
                "testc"
        ));
        assertThat(applicationPropertyDo)
                .usingRecursiveComparison(RecursiveComparisonConfiguration.builder().withIgnoredFields("value").build())
                .isEqualTo(TEST_APPLICATION_PROPERTY_2);
        assertThat(applicationPropertyDo.value()).isEqualTo("testc");

        applicationPropertyDo = applicationPropertyRepository.getApplicationProperty(
                TEST_APPLICATION_PROPERTY_1.group(),
                TEST_APPLICATION_PROPERTY_1.key(),
                TEST_APPLICATION_PROPERTY_1.language()
        ).orElseThrow();
        assertThat(applicationPropertyDo)
                .usingRecursiveComparison(RecursiveComparisonConfiguration.builder().withIgnoredFields("value").build())
                .isEqualTo(TEST_APPLICATION_PROPERTY_1);
        assertThat(applicationPropertyDo.value()).isEqualTo("testc");

        applicationPropertyDo = applicationPropertyRepository.getApplicationProperty(
                TEST_APPLICATION_PROPERTY_2.group(),
                TEST_APPLICATION_PROPERTY_2.key(),
                TEST_APPLICATION_PROPERTY_2.language()
        ).orElseThrow();
        assertThat(applicationPropertyDo)
                .usingRecursiveComparison(RecursiveComparisonConfiguration.builder().withIgnoredFields("value").build())
                .isEqualTo(TEST_APPLICATION_PROPERTY_2);
        assertThat(applicationPropertyDo.value()).isEqualTo("testc");

        applicationPropertyRepository.deleteApplicationProperty(
                TEST_APPLICATION_PROPERTY_1.group(),
                TEST_APPLICATION_PROPERTY_1.key(),
                TEST_APPLICATION_PROPERTY_1.language()
        );
        assertThat(applicationPropertyRepository.count()).isEqualTo(1L);
        assertThat(applicationPropertyRepository.exists(
                TEST_APPLICATION_PROPERTY_1.group(),
                TEST_APPLICATION_PROPERTY_1.key(),
                TEST_APPLICATION_PROPERTY_1.language())).isFalse();
        assertThat(applicationPropertyRepository.exists(
                TEST_APPLICATION_PROPERTY_2.group(),
                TEST_APPLICATION_PROPERTY_2.key(),
                TEST_APPLICATION_PROPERTY_2.language()
        )).isTrue();

        applicationPropertyRepository.deleteApplicationProperty(
                TEST_APPLICATION_PROPERTY_2.group(),
                TEST_APPLICATION_PROPERTY_2.key(),
                TEST_APPLICATION_PROPERTY_2.language()
        );
        assertThat(applicationPropertyRepository.count()).isEqualTo(0L);
        assertThat(applicationPropertyRepository.exists(
                TEST_APPLICATION_PROPERTY_1.group(),
                TEST_APPLICATION_PROPERTY_1.key(),
                TEST_APPLICATION_PROPERTY_1.language())).isFalse();
        assertThat(applicationPropertyRepository.exists(
                TEST_APPLICATION_PROPERTY_2.group(),
                TEST_APPLICATION_PROPERTY_2.key(),
                TEST_APPLICATION_PROPERTY_2.language()
        )).isFalse();
    }
}
