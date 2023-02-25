package sk.janobono.wiwa.api;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import sk.janobono.wiwa.BaseIntegrationTest;
import sk.janobono.wiwa.common.model.Authority;
import sk.janobono.wiwa.common.model.UserSo;
import sk.janobono.wiwa.business.model.user.UserDataSo;
import sk.janobono.wiwa.business.model.user.UserProfileSo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserControllerIT extends BaseIntegrationTest {

    @Test
    public void allUserControllerMethods() throws Exception {
        String token = signIn(DEFAULT_ADMIN, PASSWORD).token();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        List<UserSo> users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            users.add(addUser(headers, i));
        }

        for (UserSo userSO : users) {
            assertThat(userSO).usingRecursiveComparison().isEqualTo(getUser(headers, userSO.id()));
        }

        Pageable pageable = PageRequest.of(0, 5, Sort.Direction.DESC, "id", "username");
        Page<UserSo> page = getUsers(headers, pageable);
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

        for (UserSo userSO : users) {
            setUser(headers, userSO);
            setAuthorities(headers, userSO);
            setConfirmed(headers, userSO);
            setEnabled(headers, userSO);
            deleteUser(headers, userSO.id());
        }
    }

    private UserSo getUser(HttpHeaders headers, Long id) {
        ResponseEntity<UserSo> response = restTemplate.exchange(
                getURI("/users/{id}", Map.of("id", Long.toString(id))),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                UserSo.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return response.getBody();
    }

    private Page<UserSo> getUsers(HttpHeaders headers, Pageable pageable) {
        MultiValueMap<String, String> params = pageableToParams(pageable);
        ResponseEntity<JsonNode> response = restTemplate.exchange(
                getURI("/users", params),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                JsonNode.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return getPage(response.getBody(), pageable, UserSo.class);
    }

    private Page<UserSo> getUsers(HttpHeaders headers, String searchField, String username, String email, Pageable pageable) {
        MultiValueMap<String, String> params = pageableToParams(pageable);
        params.add("search-field", searchField);
        params.add("username", username);
        params.add("email", email);
        ResponseEntity<JsonNode> response = restTemplate.exchange(
                getURI("/users/by-search-criteria", params),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                JsonNode.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return getPage(response.getBody(), pageable, UserSo.class);
    }

    private UserSo addUser(HttpHeaders headers, int index) {
        ResponseEntity<UserSo> response = restTemplate.exchange(
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
                UserSo.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        return response.getBody();
    }

    private void setUser(HttpHeaders headers, UserSo userDto) {
        ResponseEntity<UserSo> response = restTemplate.exchange(
                getURI("/users/{id}", Map.of("id", Long.toString(userDto.id()))),
                HttpMethod.PUT,
                new HttpEntity<>(new UserProfileSo(
                        userDto.titleBefore() + "changed",
                        userDto.firstName() + "changed",
                        null,
                        userDto.lastName() + "changed",
                        userDto.titleAfter() + "changed"
                ), headers),
                UserSo.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        UserSo result = response.getBody();
        assertThat(result.midName()).isNull();
    }

    private void setAuthorities(HttpHeaders headers, UserSo userDto) {
        ResponseEntity<UserSo> response = restTemplate.exchange(
                getURI("/users/{id}/authorities", Map.of("id", Long.toString(userDto.id()))),
                HttpMethod.PATCH,
                new HttpEntity<>(
                        new Authority[]{Authority.W_CUSTOMER, Authority.W_EMPLOYEE}
                        , headers),
                UserSo.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().authorities().size()).isEqualTo(2);
    }

    private void setConfirmed(HttpHeaders headers, UserSo userDto) {
        ResponseEntity<UserSo> response = restTemplate.exchange(
                getURI("/users/{id}/confirm", Map.of("id", Long.toString(userDto.id()))),
                HttpMethod.PATCH,
                new HttpEntity<>(Boolean.TRUE, headers),
                UserSo.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().confirmed()).isTrue();
    }

    private void setEnabled(HttpHeaders headers, UserSo userDto) {
        ResponseEntity<UserSo> response = restTemplate.exchange(
                getURI("/users/{id}/enable", Map.of("id", Long.toString(userDto.id()))),
                HttpMethod.PATCH,
                new HttpEntity<>(Boolean.TRUE, headers),
                UserSo.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().enabled()).isTrue();
    }

    private void deleteUser(HttpHeaders headers, Long id) {
        ResponseEntity<Void> response = restTemplate.exchange(
                getURI("/users/{id}", Map.of("id", Long.toString(id))),
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
