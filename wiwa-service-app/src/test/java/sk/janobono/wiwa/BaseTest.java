package sk.janobono.wiwa;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import sk.janobono.wiwa.api.model.auth.AuthenticationResponseWebDto;
import sk.janobono.wiwa.business.impl.util.MailUtilService;
import sk.janobono.wiwa.business.model.auth.SignInData;
import sk.janobono.wiwa.dal.domain.AuthorityDo;
import sk.janobono.wiwa.dal.domain.UserDo;
import sk.janobono.wiwa.dal.repository.*;
import sk.janobono.wiwa.model.Authority;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration",
                "spring.flyway.enabled=false"
        }
)
public abstract class BaseTest {

    public static final String DEFAULT_ADMIN = "wiwa";
    public static final String DEFAULT_MANAGER = "wmanager";
    public static final String DEFAULT_EMPLOYEE = "wemployee";
    public static final String DEFAULT_CUSTOMER = "wcustomer";
    public static final String PASSWORD = "wiwa";

    @Value("${local.server.port}")
    public int serverPort;

    @Autowired
    public PasswordEncoder passwordEncoder;

    @Autowired
    public ObjectMapper objectMapper;

    public RestClient restClient;

    @MockBean
    public InitDataCommandLineRunner initDataCommandLineRunner;

    @MockBean
    public MailUtilService mailUtilService;

    @MockBean
    public ApplicationImageRepository applicationImageRepository;

    @MockBean
    public ApplicationPropertyRepository applicationPropertyRepository;

    @MockBean
    public AuthorityRepository authorityRepository;

    @MockBean
    public BoardCodeListItemRepository boardCodeListItemRepository;

    @MockBean
    public BoardImageRepository boardImageRepository;

    @MockBean
    public BoardRepository boardRepository;

    @MockBean
    public CodeListItemRepository codeListItemRepository;

    @MockBean
    public CodeListRepository codeListRepository;

    @MockBean
    public EdgeCodeListItemRepository edgeCodeListItemRepository;

    @MockBean
    public EdgeImageRepository edgeImageRepository;

    @MockBean
    public EdgeRepository edgeRepository;

    @MockBean
    public OrderCommentRepository orderCommentRepository;

    @MockBean
    public OrderItemRepository orderItemRepository;

    @MockBean
    public OrderItemSummaryRepository orderItemSummaryRepository;

    @MockBean
    public OrderMaterialRepository orderMaterialRepository;

    @MockBean
    public OrderNumberRepository orderNumberRepository;

    @MockBean
    public OrderRepository orderRepository;

    @MockBean
    public OrderStatusRepository orderStatusRepository;

    @MockBean
    public OrderSummaryViewRepository orderSummaryViewRepository;

    @MockBean
    public OrderViewRepository orderViewRepository;

    @MockBean
    public UserRepository userRepository;

    protected TestMail testMail;

    @BeforeEach
    public void setUp() {
        restClient = RestClient.create("http://localhost:" + serverPort);

        testMail = new TestMail();
        testMail.mock(mailUtilService);

        final TestRepositories testRepositories = new TestRepositories();
        testRepositories.mock(applicationImageRepository);
        testRepositories.mock(applicationPropertyRepository);
        testRepositories.mock(authorityRepository);
        testRepositories.mock(boardCodeListItemRepository);
        testRepositories.mock(boardImageRepository);
        testRepositories.mock(boardRepository);
        testRepositories.mock(codeListItemRepository);
        testRepositories.mock(codeListRepository);
        testRepositories.mock(edgeCodeListItemRepository);
        testRepositories.mock(edgeImageRepository);
        testRepositories.mock(edgeRepository);
        testRepositories.mock(orderCommentRepository);
        testRepositories.mock(orderItemRepository);
        testRepositories.mock(orderItemSummaryRepository);
        testRepositories.mock(orderMaterialRepository);
        testRepositories.mock(orderNumberRepository);
        testRepositories.mock(orderRepository);
        testRepositories.mock(orderStatusRepository);
        testRepositories.mock(orderSummaryViewRepository);
        testRepositories.mock(orderViewRepository);
        testRepositories.mock(userRepository);

        for (final Authority authority : Authority.values()) {
            authorityRepository.save(AuthorityDo.builder().authority(authority).build());
        }
        addUser(DEFAULT_ADMIN, Authority.W_ADMIN);
        addUser(DEFAULT_MANAGER, Authority.W_MANAGER);
        addUser(DEFAULT_EMPLOYEE, Authority.W_EMPLOYEE);
        addUser(DEFAULT_CUSTOMER, Authority.W_CUSTOMER);
    }

    public AuthenticationResponseWebDto signIn(final String username, final String password) {
        return restClient
                .post()
                .uri(getURI("/auth/sign-in"))
                .contentType(MediaType.APPLICATION_JSON)
                .body(new SignInData(username, password))
                .retrieve().body(AuthenticationResponseWebDto.class);
    }

    public URI getURI(final String path) {
        return UriComponentsBuilder.fromHttpUrl("http://localhost:" + serverPort)
                .path("/api" + path).build().toUri();
    }

    public URI getURI(final String path, final Map<String, String> pathVars) {
        return UriComponentsBuilder.fromHttpUrl("http://localhost:" + serverPort)
                .path("/api" + path).buildAndExpand(pathVars).toUri();
    }

    public URI getURI(final String path, final MultiValueMap<String, String> queryParams) {
        return UriComponentsBuilder.fromHttpUrl("http://localhost:" + serverPort)
                .path("/api" + path).queryParams(queryParams).build().toUri();
    }

    public URI getURI(final String path, final Map<String, String> pathVars, final MultiValueMap<String, String> queryParams) {
        return UriComponentsBuilder.fromHttpUrl("http://localhost:" + serverPort)
                .path("/api" + path).queryParams(queryParams).buildAndExpand(pathVars).toUri();
    }

    public <T> Page<T> getPage(final JsonNode jsonNode, final Pageable pageable, final Class<T> clazz) {
        return Optional.ofNullable(jsonNode)
                .map(jsonNode1 -> {
                    final long totalElements = jsonNode.get("totalElements").asLong();
                    final List<T> content;
                    try {
                        content = getListFromNode(jsonNode.get("content"), clazz);
                    } catch (final IOException e) {
                        throw new RuntimeException(e);
                    }
                    return new PageImpl<>(content, pageable, totalElements);
                })
                .orElse(new PageImpl<>(new ArrayList<>(), pageable, 0));
    }

    public <T> List<T> getListFromNode(final JsonNode node, final Class<T> clazz) throws IOException {
        final List<T> content = new ArrayList<>();
        for (final JsonNode val : node) {
            content.add(objectMapper.readValue(val.traverse(), clazz));
        }
        return content;
    }

    public void addToParams(final MultiValueMap<String, String> params, final String key, final String value) {
        Optional.ofNullable(value).ifPresent(v -> params.add(key, v));
    }

    public void addToParams(final MultiValueMap<String, String> params, final String key, final Long value) {
        addToParams(params, key, Optional.ofNullable(value).map(Object::toString).orElse(null));
    }

    public void addToParams(final MultiValueMap<String, String> params, final String key, final Boolean value) {
        addToParams(params, key, Optional.ofNullable(value).map(Object::toString).orElse(null));
    }

    public void addToParams(final MultiValueMap<String, String> params, final String key, final BigDecimal value) {
        addToParams(params, key, Optional.ofNullable(value).map(BigDecimal::toPlainString).orElse(null));
    }

    public void addToParams(final MultiValueMap<String, String> params, final String key, final OffsetDateTime value) {
        addToParams(params, key, Optional.ofNullable(value).map(v -> v.format(DateTimeFormatter.ISO_INSTANT)).orElse(null));
    }

    public void addToParams(final MultiValueMap<String, String> params, final String key, final List<String> value) {
        Optional.ofNullable(value).ifPresent(v -> params.add(key, String.join(",", v)));
    }

    public void addPageableToParams(final MultiValueMap<String, String> params, final Pageable pageable) {
        if (pageable.isPaged()) {
            params.add("page", Integer.toString(pageable.getPageNumber()));
            params.add("size", Integer.toString(pageable.getPageSize()));
            if (pageable.getSort().isSorted()) {
                final StringBuilder sb = new StringBuilder();
                List<Sort.Order> orderList = pageable.getSort().get().filter(Sort.Order::isAscending).collect(Collectors.toList());
                if (!orderList.isEmpty()) {
                    for (final Sort.Order order : orderList) {
                        sb.append(order.getProperty()).append(',');
                    }
                    sb.append("ASC,");
                }
                orderList = pageable.getSort().get().filter(Sort.Order::isDescending).toList();
                if (!orderList.isEmpty()) {
                    for (final Sort.Order order : orderList) {
                        sb.append(order.getProperty()).append(',');
                    }
                    sb.append("DESC,");
                }
                String sort = sb.toString();
                sort = sort.substring(0, sort.length() - 1);
                params.add("sort", sort);
            }
        }
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
}
