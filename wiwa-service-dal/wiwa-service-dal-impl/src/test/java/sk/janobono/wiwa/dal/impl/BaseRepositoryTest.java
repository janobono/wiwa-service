package sk.janobono.wiwa.dal.impl;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest(
        properties = {
                "spring.flyway.clean-disabled=false"
        }
)
public abstract class BaseRepositoryTest {

    public static final PostgreSQLContainer<?> postgresDB = new PostgreSQLContainer<>
            ("postgres:alpine")
            .withDatabaseName("app")
            .withUsername("app")
            .withPassword("app");

    static {
        postgresDB.start();
    }

    @DynamicPropertySource
    public static void properties(final DynamicPropertyRegistry registry) throws Exception {
        registry.add("spring.datasource.url", postgresDB::getJdbcUrl);
    }

    @Autowired
    public Flyway flyway;

    @BeforeEach
    public void setUp() throws Exception {
        flyway.clean();
        flyway.migrate();
    }
}
