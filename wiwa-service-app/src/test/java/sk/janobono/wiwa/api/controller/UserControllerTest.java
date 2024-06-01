package sk.janobono.wiwa.api.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import sk.janobono.wiwa.BaseTest;
import sk.janobono.wiwa.api.model.SingleValueBodyWebDto;
import sk.janobono.wiwa.api.model.user.UserCreateWebDto;
import sk.janobono.wiwa.api.model.user.UserProfileWebDto;
import sk.janobono.wiwa.api.model.user.UserWebDto;
import sk.janobono.wiwa.model.Authority;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class UserControllerTest extends BaseTest {

    @Test
    public void allUserControllerMethods() {
        final String token = signIn(DEFAULT_ADMIN, PASSWORD).token();

        final List<UserWebDto> users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            users.add(addUser(token, i));
        }

        for (final UserWebDto user : users) {
            assertThat(user).usingRecursiveComparison().isEqualTo(getUser(token, user.id()));
        }

        Page<UserWebDto> page = getUsers(token);
        assertThat(page.getTotalElements()).isEqualTo(14);
        assertThat(page.getTotalPages()).isEqualTo(1);
        assertThat(page.getContent()).hasSize(14);

        for (final UserWebDto user : users) {
            setUser(token, user);
            setAuthorities(token, user.id());
            setConfirmed(token, user.id());
            setEnabled(token, user.id());
            deleteUser(token, user.id());
        }

        page = getUsers(token);
        assertThat(page.getTotalElements()).isEqualTo(4);
        assertThat(page.getTotalPages()).isEqualTo(1);
        assertThat(page.getContent()).hasSize(4);
    }

    private UserWebDto getUser(final String token, final Long id) {
        return restClient.get()
                .uri(getURI("/users/{id}", Map.of("id", id.toString())))
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(UserWebDto.class);
    }

    private Page<UserWebDto> getUsers(final String token) {
        final LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        addPageableToParams(params, Pageable.unpaged());
        final JsonNode jsonNode = restClient.get()
                .uri(getURI("/users", params))
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(JsonNode.class);
        return getPage(jsonNode, Pageable.unpaged(), UserWebDto.class);
    }

    private UserWebDto addUser(final String token, final int index) {
        return restClient.post()
                .uri(getURI("/users"))
                .header("Authorization", "Bearer " + token)
                .body(new UserCreateWebDto(
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
                ))
                .retrieve()
                .body(UserWebDto.class);
    }

    private void setUser(final String token, final UserWebDto user) {
        final ResponseEntity<UserWebDto> response = restClient.put()
                .uri(getURI("/users/{id}", Map.of("id", user.id().toString())))
                .header("Authorization", "Bearer " + token)
                .body(new UserProfileWebDto(
                        user.titleBefore() + "changed",
                        user.firstName() + "changed",
                        null,
                        user.lastName() + "changed",
                        user.titleAfter() + "changed"
                ))
                .retrieve()
                .toEntity(UserWebDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().titleBefore()).endsWith("changed");
        assertThat(response.getBody().firstName()).endsWith("changed");
        assertThat(response.getBody().midName()).isNull();
        assertThat(response.getBody().lastName()).endsWith("changed");
        assertThat(response.getBody().titleAfter()).endsWith("changed");
    }

    private void setAuthorities(final String token, final Long id) {
        final ResponseEntity<UserWebDto> response = restClient.patch()
                .uri(getURI("/users/{id}/authorities", Map.of("id", id.toString())))
                .header("Authorization", "Bearer " + token)
                .body(List.of(Authority.W_CUSTOMER, Authority.W_EMPLOYEE))
                .retrieve()
                .toEntity(UserWebDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().authorities()).hasSize(2);
    }

    private void setConfirmed(final String token, final Long id) {
        final ResponseEntity<UserWebDto> response = restClient.patch()
                .uri(getURI("/users/{id}/confirm", Map.of("id", id.toString())))
                .header("Authorization", "Bearer " + token)
                .body(new SingleValueBodyWebDto<>(true))
                .retrieve()
                .toEntity(UserWebDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().confirmed()).isTrue();
    }

    private void setEnabled(final String token, final Long id) {
        final ResponseEntity<UserWebDto> response = restClient.patch()
                .uri(getURI("/users/{id}/enable", Map.of("id", id.toString())))
                .header("Authorization", "Bearer " + token)
                .body(new SingleValueBodyWebDto<>(true))
                .retrieve()
                .toEntity(UserWebDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().enabled()).isTrue();
    }

    private void deleteUser(final String token, final Long id) {
        restClient.delete()
                .uri(getURI("/users/{id}", Map.of("id", id.toString())))
                .header("Authorization", "Bearer " + token)
                .retrieve();
    }
}
