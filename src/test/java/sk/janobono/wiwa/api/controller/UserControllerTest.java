package sk.janobono.wiwa.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import sk.janobono.wiwa.api.model.SingleValueBodyWebDto;
import sk.janobono.wiwa.api.model.user.UserCreateWebDto;
import sk.janobono.wiwa.api.model.user.UserProfileWebDto;
import sk.janobono.wiwa.api.model.user.UserWebDto;
import sk.janobono.wiwa.model.Authority;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class UserControllerTest extends BaseControllerTest {

    @Test
    public void allUserControllerMethods() throws Exception {
        final String token = signIn(DEFAULT_ADMIN, PASSWORD).token();

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        final List<UserWebDto> users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            users.add(addUser(headers, i));
        }

        for (final UserWebDto user : users) {
            assertThat(user).usingRecursiveComparison().isEqualTo(getUser(headers, user.id()));
        }

        Pageable pageable = PageRequest.of(0, 5, Sort.Direction.DESC, "id", "username");
        Page<UserWebDto> page = getUsers(headers, pageable);
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

        for (final UserWebDto user : users) {
            setUser(headers, user);
            setAuthorities(headers, user);
            setConfirmed(headers, user);
            setEnabled(headers, user);
            deleteUser(headers, user.id());
        }
    }

    private UserWebDto getUser(final HttpHeaders headers, final Long id) {
        return getEntity(UserWebDto.class, headers, "/users", id);
    }

    private Page<UserWebDto> getUsers(final HttpHeaders headers, final Pageable pageable) {
        return getEntities(UserWebDto.class, headers, "/users", new LinkedMultiValueMap<>(), pageable);
    }

    private Page<UserWebDto> getUsers(final HttpHeaders headers, final String searchField, final String username, final String email, final Pageable pageable) {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        addToParams(params, "searchField", searchField);
        addToParams(params, "username", username);
        addToParams(params, "email", email);
        return getEntities(UserWebDto.class, headers, "/users", params, pageable);
    }

    private UserWebDto addUser(final HttpHeaders headers, final int index) {
        return addEntity(UserWebDto.class, headers, "/users", new UserCreateWebDto(
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
                List.of(Authority.W_CUSTOMER)
        ));
    }

    private UserWebDto setUser(final HttpHeaders headers, final UserWebDto user) {
        return setEntity(UserWebDto.class, headers, "/users", user.id(),
                new UserProfileWebDto(
                        user.titleBefore() + "changed",
                        user.firstName() + "changed",
                        null,
                        user.lastName() + "changed",
                        user.titleAfter() + "changed"
                ));
    }

    private void setAuthorities(final HttpHeaders headers, final UserWebDto user) {
        final ResponseEntity<UserWebDto> response = restTemplate.exchange(
                getURI("/users/{id}/authorities", Map.of("id", Long.toString(user.id()))),
                HttpMethod.PATCH,
                new HttpEntity<>(
                        new Authority[]{Authority.W_CUSTOMER, Authority.W_EMPLOYEE}
                        , headers),
                UserWebDto.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().authorities().size()).isEqualTo(2);
    }

    private void setConfirmed(final HttpHeaders headers, final UserWebDto user) {
        final ResponseEntity<UserWebDto> response = restTemplate.exchange(
                getURI("/users/{id}/confirm", Map.of("id", Long.toString(user.id()))),
                HttpMethod.PATCH,
                new HttpEntity<>(new SingleValueBodyWebDto<>(Boolean.TRUE), headers),
                UserWebDto.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().confirmed()).isTrue();
    }

    private void setEnabled(final HttpHeaders headers, final UserWebDto user) {
        final ResponseEntity<UserWebDto> response = restTemplate.exchange(
                getURI("/users/{id}/enable", Map.of("id", Long.toString(user.id()))),
                HttpMethod.PATCH,
                new HttpEntity<>(new SingleValueBodyWebDto<>(Boolean.TRUE), headers),
                UserWebDto.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().enabled()).isTrue();
    }

    private void deleteUser(final HttpHeaders headers, final Long id) {
        deleteEntity(headers, "/users", id);
    }
}
