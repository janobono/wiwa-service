package sk.janobono.wiwa.api.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import sk.janobono.wiwa.BaseIntegrationTest;
import sk.janobono.wiwa.api.model.auth.AuthenticationResponseWebDto;
import sk.janobono.wiwa.business.model.auth.SignInData;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class BaseControllerTest extends BaseIntegrationTest {

    public AuthenticationResponseWebDto signIn(final String username, final String password) {
        return restTemplate.postForObject(
                getURI("/auth/sign-in"),
                new SignInData(
                        username,
                        password
                ),
                AuthenticationResponseWebDto.class
        );
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

    public void addToParams(final MultiValueMap<String, String> params, final String key, final String value) {
        Optional.ofNullable(value).ifPresent(v -> params.add(key, v));
    }

    public void addToParams(final MultiValueMap<String, String> params, final String key, final List<String> value) {
        Optional.ofNullable(value).ifPresent(v -> params.add(key, String.join(",", v)));
    }

    protected <T> T getEntity(final Class<T> clazz, final HttpHeaders headers, final String path, final Long id) {
        final ResponseEntity<T> response = restTemplate.exchange(getURI(path + "/{id}", Map.of("id", Long.toString(id))), HttpMethod.GET, new HttpEntity<>(headers), clazz);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return response.getBody();
    }

    protected <T> Page<T> getEntities(final Class<T> clazz, final HttpHeaders headers, final String path, final MultiValueMap<String, String> params, final Pageable pageable) {
        addPageableToParams(params, pageable);
        final ResponseEntity<JsonNode> response = restTemplate.exchange(
                getURI(path, params),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                JsonNode.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return getPage(response.getBody(), pageable, clazz);
    }

    protected <T> T addEntity(final Class<T> clazz, final HttpHeaders headers, final String path, final Object entityData) {
        final ResponseEntity<T> response = restTemplate.exchange(getURI(path), HttpMethod.POST, new HttpEntity<>(entityData, headers), clazz);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        return response.getBody();
    }

    protected <T> T setEntity(final Class<T> clazz, final HttpHeaders headers, final String path, final Long id, final Object entityData) {
        final ResponseEntity<T> response = restTemplate.exchange(
                getURI(path + "/{id}", Map.of("id", id.toString())),
                HttpMethod.PUT,
                new HttpEntity<>(entityData, headers),
                clazz
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return response.getBody();
    }

    protected void deleteEntity(final HttpHeaders headers, final String path, final Long id) {
        final ResponseEntity<Void> response = restTemplate.exchange(
                getURI(path + "/{id}", Map.of("id", Long.toString(id))),
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
