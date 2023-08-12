package sk.janobono.wiwa.api.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import sk.janobono.wiwa.BaseIntegrationTest;
import sk.janobono.wiwa.api.model.SingleValueBody;
import sk.janobono.wiwa.business.model.user.UserDataSo;
import sk.janobono.wiwa.business.model.user.UserProfileSo;
import sk.janobono.wiwa.model.Authority;
import sk.janobono.wiwa.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserControllerTest extends BaseIntegrationTest {

    @Test
    public void allUserControllerMethods() throws Exception {
        final String token = signIn(DEFAULT_ADMIN, PASSWORD).token();

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        final List<User> users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            users.add(addUser(headers, i));
        }

        for (final User user : users) {
            assertThat(user).usingRecursiveComparison().isEqualTo(getUser(headers, user.id()));
        }

        Pageable pageable = PageRequest.of(0, 5, Sort.Direction.DESC, "id", "username");
        Page<User> page = getUsers(headers, pageable);
        assertThat(page.getTotalElements()).isEqualTo(14);
        assertThat(page.getTotalPages()).isEqualTo(3);
        assertThat(page.getContent().size()).isEqualTo(5);

        pageable = PageRequest.of(1, 5, Sort.Direction.DESC, "id", "username");
        page = getUsers(headers, pageable);
        assertThat(page.getTotalElements()).isEqualTo(14);
        assertThat(page.getTotalPages()).isEqualTo(3);
        assertThat(page.getContent().size()).isEqualTo(5);

        pageable = PageRequest.of(0, 5);
        page = getUsers(headers, "0", "user0", "mail0@domain.com", pageable);
        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getTotalPages()).isEqualTo(1);
        assertThat(page.getContent().size()).isEqualTo(1);

        for (final User user : users) {
            setUser(headers, user);
            setAuthorities(headers, user);
            setConfirmed(headers, user);
            setEnabled(headers, user);
            deleteUser(headers, user.id());
        }
    }

    private User getUser(final HttpHeaders headers, final Long id) {
        final ResponseEntity<User> response = restTemplate.exchange(
                getURI("/users/{id}", Map.of("id", Long.toString(id))),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                User.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return response.getBody();
    }

    private Page<User> getUsers(final HttpHeaders headers, final Pageable pageable) {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        addPageableToParams(params, pageable);
        final ResponseEntity<JsonNode> response = restTemplate.exchange(
                getURI("/users", params),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                JsonNode.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return getPage(response.getBody(), pageable, User.class);
    }

    private Page<User> getUsers(final HttpHeaders headers, final String searchField, final String username, final String email, final Pageable pageable) {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        addToParams(params, "search-field", searchField);
        addToParams(params, "username", username);
        addToParams(params, "email", email);
        addPageableToParams(params, pageable);
        final ResponseEntity<JsonNode> response = restTemplate.exchange(
                getURI("/users", params),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                JsonNode.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return getPage(response.getBody(), pageable, User.class);
    }

    private User addUser(final HttpHeaders headers, final int index) {
        final ResponseEntity<User> response = restTemplate.exchange(
                getURI("/users"),
                HttpMethod.POST,
                new HttpEntity<>(new UserDataSo(
                        "user" + index,
                        "before" + index,
                        "First" + index,
                        "Mid" + index,
                        "Last" + index,
                        "after" + index,
                        "mail" + index + "@domain.com",
                        true,
                        false,
                        false,
                        Set.of(Authority.W_CUSTOMER)
                ), headers),
                User.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        return response.getBody();
    }

    private void setUser(final HttpHeaders headers, final User userDto) {
        final ResponseEntity<User> response = restTemplate.exchange(
                getURI("/users/{id}", Map.of("id", Long.toString(userDto.id()))),
                HttpMethod.PUT,
                new HttpEntity<>(new UserProfileSo(
                        userDto.titleBefore() + "changed",
                        userDto.firstName() + "changed",
                        null,
                        userDto.lastName() + "changed",
                        userDto.titleAfter() + "changed"
                ), headers),
                User.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        final User result = response.getBody();
        assertThat(result.midName()).isNull();
    }

    private void setAuthorities(final HttpHeaders headers, final User userDto) {
        final ResponseEntity<User> response = restTemplate.exchange(
                getURI("/users/{id}/authorities", Map.of("id", Long.toString(userDto.id()))),
                HttpMethod.PATCH,
                new HttpEntity<>(
                        new SingleValueBody<>(new Authority[]{Authority.W_CUSTOMER, Authority.W_EMPLOYEE})
                        , headers),
                User.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().authorities().size()).isEqualTo(2);
    }

    private void setConfirmed(final HttpHeaders headers, final User userDto) {
        final ResponseEntity<User> response = restTemplate.exchange(
                getURI("/users/{id}/confirm", Map.of("id", Long.toString(userDto.id()))),
                HttpMethod.PATCH,
                new HttpEntity<>(new SingleValueBody<>(Boolean.TRUE), headers),
                User.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().confirmed()).isTrue();
    }

    private void setEnabled(final HttpHeaders headers, final User userDto) {
        final ResponseEntity<User> response = restTemplate.exchange(
                getURI("/users/{id}/enable", Map.of("id", Long.toString(userDto.id()))),
                HttpMethod.PATCH,
                new HttpEntity<>(new SingleValueBody<>(Boolean.TRUE), headers),
                User.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().enabled()).isTrue();
    }

    private void deleteUser(final HttpHeaders headers, final Long id) {
        final ResponseEntity<Void> response = restTemplate.exchange(
                getURI("/users/{id}", Map.of("id", Long.toString(id))),
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
