package sk.janobono.wiwa.dal.repository;

import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
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
import sk.janobono.wiwa.dal.domain.ApplicationImageDo;
import sk.janobono.wiwa.dal.model.ApplicationImageInfoDo;

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
class ApplicationImageRepositoryImplIT {

    private static final ApplicationImageDo TEST_APPLICATION_IMAGE_1 = new ApplicationImageDo(
            "filename1", "filetype1", "data1".getBytes(), "data1".getBytes()
    );
    private static final ApplicationImageDo TEST_APPLICATION_IMAGE_2 = new ApplicationImageDo(
            "filename2", "filetype2", "data2".getBytes(), "data2".getBytes()
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
    public ApplicationImageRepository applicationImageRepository;

    @Test
    void fullTest() {
        assertThat(applicationImageRepository).isNotNull();

        assertThat(applicationImageRepository.exists(TEST_APPLICATION_IMAGE_1.fileName())).isFalse();
        assertThat(applicationImageRepository.exists(TEST_APPLICATION_IMAGE_2.fileName())).isFalse();

        ApplicationImageInfoDo applicationImageInfoDo;
        applicationImageInfoDo = applicationImageRepository.addApplicationImage(TEST_APPLICATION_IMAGE_1);
        assertThat(applicationImageInfoDo).usingRecursiveComparison(
                RecursiveComparisonConfiguration.builder().withIgnoredFields("data").build()
        ).isEqualTo(TEST_APPLICATION_IMAGE_1);
        applicationImageInfoDo = applicationImageRepository.addApplicationImage(TEST_APPLICATION_IMAGE_2);
        assertThat(applicationImageInfoDo).usingRecursiveComparison(
                RecursiveComparisonConfiguration.builder().withIgnoredFields("data").build()
        ).isEqualTo(TEST_APPLICATION_IMAGE_2);

        assertThat(applicationImageRepository.exists(TEST_APPLICATION_IMAGE_1.fileName())).isTrue();
        assertThat(applicationImageRepository.exists(TEST_APPLICATION_IMAGE_2.fileName())).isTrue();

        ApplicationImageDo applicationImageDo;
        applicationImageDo = applicationImageRepository.getApplicationImage(TEST_APPLICATION_IMAGE_1.fileName()).orElseThrow();
        assertThat(applicationImageDo).usingRecursiveComparison().isEqualTo(TEST_APPLICATION_IMAGE_1);
        applicationImageDo = applicationImageRepository.getApplicationImage(TEST_APPLICATION_IMAGE_2.fileName()).orElseThrow();
        assertThat(applicationImageDo).usingRecursiveComparison().isEqualTo(TEST_APPLICATION_IMAGE_2);

        applicationImageInfoDo = applicationImageRepository.setApplicationImage(
                new ApplicationImageDo(TEST_APPLICATION_IMAGE_1.fileName(), "test1c", "test1c".getBytes(), "test1c".getBytes())
        );
        assertThat(applicationImageInfoDo).usingRecursiveComparison(
                RecursiveComparisonConfiguration.builder().withComparedFields("fileName").build()
        ).isEqualTo(TEST_APPLICATION_IMAGE_1);
        assertThat(applicationImageInfoDo.fileType()).isEqualTo("test1c");
        assertThat(applicationImageInfoDo.thumbnail()).isEqualTo("test1c".getBytes());

        Page<ApplicationImageInfoDo> page = applicationImageRepository.getApplicationImages(Pageable.unpaged());
        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent().get(0).fileName()).isEqualTo(TEST_APPLICATION_IMAGE_1.fileName());
        assertThat(page.getContent().get(1).fileName()).isEqualTo(TEST_APPLICATION_IMAGE_2.fileName());

        page = applicationImageRepository.getApplicationImages(PageRequest.of(0, 5, Sort.Direction.DESC, "fileName"));
        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent().get(0).fileName()).isEqualTo(TEST_APPLICATION_IMAGE_2.fileName());
        assertThat(page.getContent().get(1).fileName()).isEqualTo(TEST_APPLICATION_IMAGE_1.fileName());
    }
}
