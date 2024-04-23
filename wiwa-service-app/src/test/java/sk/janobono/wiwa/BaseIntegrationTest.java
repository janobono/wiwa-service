package sk.janobono.wiwa;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import sk.janobono.wiwa.dal.domain.UserDo;
import sk.janobono.wiwa.dal.repository.AuthorityRepository;
import sk.janobono.wiwa.dal.repository.UserRepository;
import sk.janobono.wiwa.model.Authority;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.flyway.clean-disabled=false",
                "app.common.init-data-path=./data"
        }
)
public abstract class BaseIntegrationTest {

    public static final PostgreSQLContainer<?> postgresDB = new PostgreSQLContainer<>
            ("postgres:16-alpine")
            .withDatabaseName("app")
            .withUsername("app")
            .withPassword("app");

    public static final GenericContainer<?> smtpServer = new GenericContainer<>
            ("maildev/maildev:latest")
            .withExposedPorts(1025, 1080);

    static {
        postgresDB.start();
        smtpServer.start();
    }

    @DynamicPropertySource
    public static void properties(final DynamicPropertyRegistry registry) throws Exception {
        registry.add("spring.datasource.url", postgresDB::getJdbcUrl);
        registry.add("spring.mail.port", () -> smtpServer.getMappedPort(1025));
    }

    public record TestEmail(
            String id,
            LocalDateTime time,
            List<MailAddress> from,
            List<MailAddress> to,
            String subject,
            String text,
            String html,
            List<Attachment> attachments
    ) {
        public record MailAddress(String address, String name) {
        }

        public record Attachment(String contentType, String fileName) {
        }
    }

    public static final String DEFAULT_ADMIN = "wiwa";
    public static final String DEFAULT_MANAGER = "wmanager";
    public static final String DEFAULT_EMPLOYEE = "wemployee";
    public static final String DEFAULT_CUSTOMER = "wcustomer";
    public static final String PASSWORD = "wiwa";

    @Value("${local.server.port}")
    public int serverPort;

    @Autowired
    public Flyway flyway;

    @Autowired
    public PasswordEncoder passwordEncoder;

    @Autowired
    public InitDataCommandLineRunner initDataCommandLineRunner;

    @Autowired
    public AuthorityRepository authorityRepository;

    @Autowired
    public UserRepository userRepository;

    @Autowired
    public ObjectMapper objectMapper;

    @Autowired
    public RestTemplate restTemplate;

    @BeforeEach
    public void setUp() throws Exception {
        flyway.clean();
        flyway.migrate();
        initDataCommandLineRunner.run();
        addUser(DEFAULT_MANAGER, Authority.W_MANAGER);
        addUser(DEFAULT_EMPLOYEE, Authority.W_EMPLOYEE);
        addUser(DEFAULT_CUSTOMER, Authority.W_CUSTOMER);

        deleteAllEmails();
    }

    private void addUser(final String username, final Authority authority) {
        final UserDo userDo = userRepository.save(UserDo.builder()
                .username(username)
                .password(passwordEncoder.encode(PASSWORD))
                .firstName("wiwa")
                .lastName("wiwa")
                .email(username + "@wiwa.sk")
                .gdpr(true)
                .confirmed(true)
                .enabled(true).build()
        );
        authorityRepository.saveUserAuthorities(userDo.getId(), List.of(authority));
    }

    public void deleteAllEmails() {
        restTemplate.delete(
                UriComponentsBuilder.fromHttpUrl("http://localhost:" + smtpServer.getMappedPort(1080))
                        .path("/email/all").build().toUri()
        );
    }

    public TestEmail[] getAllEmails() {
        try {
            Thread.sleep(3000);
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }

        return restTemplate.getForObject(
                UriComponentsBuilder.fromHttpUrl("http://localhost:" + smtpServer.getMappedPort(1080))
                        .path("/email").build().toUri(),
                TestEmail[].class
        );
    }
}
