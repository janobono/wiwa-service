package sk.janobono.wiwa;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import sk.janobono.wiwa.business.model.auth.AuthenticationResponseSo;
import sk.janobono.wiwa.business.model.auth.SignInSo;
import sk.janobono.wiwa.common.model.Authority;
import sk.janobono.wiwa.dal.domain.UserDo;
import sk.janobono.wiwa.dal.repository.UserRepository;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.flyway.clean-disabled=false",
                "app.common.init-data-path=../data"
        }
)
public abstract class BaseIntegrationTest {

    public static PostgreSQLContainer<?> postgresDB = new PostgreSQLContainer<>
            ("postgres:15-alpine")
            .withDatabaseName("app")
            .withUsername("app")
            .withPassword("app");

    public static GenericContainer<?> smtpServer = new GenericContainer<>
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
        userRepository.addUser(new UserDo(
                null,
                username,
                passwordEncoder.encode(PASSWORD),
                null,
                "wiwa",
                null,
                "wiwa",
                null,
                username + "@wiwa.sk",
                true,
                true,
                true,
                Set.of(authority)
        ));
    }

    public AuthenticationResponseSo signIn(final String username, final String password) {
        return restTemplate.postForObject(
                getURI("/auth/sign-in"),
                new SignInSo(
                        username,
                        password
                ),
                AuthenticationResponseSo.class
        );
    }

    public void deleteAllEmails() {
        restTemplate.delete(
                UriComponentsBuilder.fromHttpUrl("http://localhost:" + smtpServer.getMappedPort(1080))
                        .path("/email/all").build().toUri()
        );
    }

    public TestEmail[] getAllEmails() {
        return restTemplate.getForObject(
                UriComponentsBuilder.fromHttpUrl("http://localhost:" + smtpServer.getMappedPort(1080))
                        .path("/email").build().toUri(),
                TestEmail[].class
        );
    }

    public URI getURI(final String path) {
        final URI uri = UriComponentsBuilder.fromHttpUrl("http://localhost:" + serverPort)
                .path("/api" + path).build().toUri();
        log.debug("{}", uri);
        return uri;
    }

    public URI getURI(final String path, final Map<String, String> pathVars) {
        final URI uri = UriComponentsBuilder.fromHttpUrl("http://localhost:" + serverPort)
                .path("/api" + path).buildAndExpand(pathVars).toUri();
        log.debug("{}", uri);
        return uri;
    }

    public URI getURI(final String path, final MultiValueMap<String, String> queryParams) {
        final URI uri = UriComponentsBuilder.fromHttpUrl("http://localhost:" + serverPort)
                .path("/api" + path).queryParams(queryParams).build().toUri();
        log.debug("{}", uri);
        return uri;
    }

    public URI getURI(final String path, final Map<String, String> pathVars, final MultiValueMap<String, String> queryParams) {
        final URI uri = UriComponentsBuilder.fromHttpUrl("http://localhost:" + serverPort)
                .path("/api" + path).queryParams(queryParams).buildAndExpand(pathVars).toUri();
        log.debug("{}", uri);
        return uri;
    }

    public MultiValueMap<String, String> enQueryParams() {
        final MultiValueMap<String, String> result = new LinkedMultiValueMap<>();
        result.add(LocaleChangeInterceptor.DEFAULT_PARAM_NAME, "en_US");
        return result;
    }

    public MultiValueMap<String, String> skQueryParams() {
        final MultiValueMap<String, String> result = new LinkedMultiValueMap<>();
        result.add(LocaleChangeInterceptor.DEFAULT_PARAM_NAME, "sk_SK");
        return result;
    }

    public <T> Page<T> getPage(final JsonNode jsonNode, final Pageable pageable, final Class<T> clazz) {
        List<T> content = null;
        Long totalElements = null;
        if (Objects.nonNull(jsonNode)) {
            totalElements = jsonNode.get("totalElements").asLong();
            try {
                content = getListFromNode(jsonNode.get("content"), clazz);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
        final Page<T> result;
        if (Objects.nonNull(content) && !content.isEmpty()) {
            result = new PageImpl<>(content, pageable, totalElements);
        } else {
            result = new PageImpl<>(new ArrayList<>(), pageable, 0);
        }
        return result;
    }

    public <T> List<T> getListFromNode(final JsonNode node, final Class<T> clazz) throws IOException {
        final List<T> content = new ArrayList<>();
        for (final JsonNode val : node) {
            content.add(objectMapper.readValue(val.traverse(), clazz));
        }
        return content;
    }

    public MultiValueMap<String, String> pageableToParams(final Pageable pageable) {
        final MultiValueMap<String, String> result = new LinkedMultiValueMap<>();
        if (pageable.isPaged()) {
            result.add("page", Integer.toString(pageable.getPageNumber()));
            result.add("size", Integer.toString(pageable.getPageSize()));
            if (pageable.getSort().isSorted()) {
                final StringBuilder sb = new StringBuilder();
                List<Sort.Order> orderList = pageable.getSort().get().filter(Sort.Order::isAscending).collect(Collectors.toList());
                if (!orderList.isEmpty()) {
                    for (final Sort.Order order : orderList) {
                        sb.append(order.getProperty()).append(',');
                    }
                    sb.append("ASC,");
                }
                orderList = pageable.getSort().get().filter(Sort.Order::isDescending).collect(Collectors.toList());
                if (!orderList.isEmpty()) {
                    for (final Sort.Order order : orderList) {
                        sb.append(order.getProperty()).append(',');
                    }
                    sb.append("DESC,");
                }
                String sort = sb.toString();
                sort = sort.substring(0, sort.length() - 1);
                result.add("sort", sort);
            }
        }
        return result;
    }
}
