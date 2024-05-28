package sk.janobono.wiwa.dal.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import sk.janobono.wiwa.dal.domain.UserDo;
import sk.janobono.wiwa.dal.model.UserSearchCriteriaDo;
import sk.janobono.wiwa.dal.repository.UserRepository;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class UserRepositoryTest extends BaseRepositoryTest {

    @Autowired
    public UserRepository userRepository;

    @Test
    void fullTest() {
        final List<UserDo> users = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            users.add(userRepository.save(UserDo.builder()
                    .username("username%d".formatted(i))
                    .password("password%d".formatted(i))
                    .titleBefore("titleBefore%d".formatted(i))
                    .firstName("firstName%d".formatted(i))
                    .midName("midName%d".formatted(i))
                    .lastName("lastName%d".formatted(i))
                    .titleAfter("titleAfter%d".formatted(i))
                    .email("email%d".formatted(i))
                    .gdpr(i % 2 == 0)
                    .confirmed(i % 3 == 0)
                    .enabled(i % 4 == 0)
                    .build()));
        }

        assertThat(userRepository.count()).isEqualTo(10);
        assertThat(userRepository.existsById(-1L)).isFalse();
        assertThat(userRepository.existsByEmail("NOT FOUND")).isFalse();
        assertThat(userRepository.existsByUsername("NOT FOUND")).isFalse();

        for (final UserDo user : users) {
            Optional<UserDo> searchResult = userRepository.findById(user.getId());
            assertThat(searchResult.isPresent()).isTrue();
            assertThat(user).usingRecursiveComparison().isEqualTo(searchResult.get());
            searchResult = userRepository.findByEmail(user.getEmail());
            assertThat(searchResult.isPresent()).isTrue();
            assertThat(user).usingRecursiveComparison().isEqualTo(searchResult.get());
            searchResult = userRepository.findByUsername(user.getUsername());
            assertThat(searchResult.isPresent()).isTrue();
            assertThat(user).usingRecursiveComparison().isEqualTo(searchResult.get());

            assertThat(userRepository.existsById(user.getId())).isTrue();
            assertThat(userRepository.existsByEmail(user.getEmail())).isTrue();
            assertThat(userRepository.existsByUsername(user.getUsername())).isTrue();
        }

        Page<UserDo> searchResult = userRepository.findAll(UserSearchCriteriaDo.builder().build(), Pageable.unpaged());
        assertThat(searchResult.getTotalElements()).isEqualTo(10);
        assertThat(searchResult.getTotalPages()).isEqualTo(1);
        assertThat(searchResult.getSize()).isEqualTo(10);
        assertThat(searchResult.getContent().size()).isEqualTo(10);
        assertThat(searchResult.getContent().getFirst()).usingRecursiveComparison().isEqualTo(users.getFirst());

        searchResult = userRepository.findAll(UserSearchCriteriaDo.builder().build(), PageRequest.of(0, 3, Sort.Direction.ASC, "anything"));
        assertThat(searchResult.getTotalElements()).isEqualTo(10);
        assertThat(searchResult.getTotalPages()).isEqualTo(4);
        assertThat(searchResult.getSize()).isEqualTo(3);
        assertThat(searchResult.getContent().size()).isEqualTo(3);
        assertThat(searchResult.getContent().getFirst()).usingRecursiveComparison().isEqualTo(users.getFirst());

        searchResult = userRepository.findAll(UserSearchCriteriaDo.builder().build(), PageRequest.of(0, 3, Sort.Direction.DESC, "anything"));
        assertThat(searchResult.getTotalElements()).isEqualTo(10);
        assertThat(searchResult.getTotalPages()).isEqualTo(4);
        assertThat(searchResult.getSize()).isEqualTo(3);
        assertThat(searchResult.getContent().size()).isEqualTo(3);
        assertThat(searchResult.getContent().getFirst()).usingRecursiveComparison().isEqualTo(users.getLast());

        searchResult = userRepository.findAll(UserSearchCriteriaDo.builder()
                .searchField("userName0")
                .build(), Pageable.unpaged());
        assertThat(searchResult.getTotalElements()).isEqualTo(1);
        assertThat(searchResult.getTotalPages()).isEqualTo(1);
        assertThat(searchResult.getSize()).isEqualTo(1);
        assertThat(searchResult.getContent().size()).isEqualTo(1);

        searchResult = userRepository.findAll(UserSearchCriteriaDo.builder()
                .username("userName0")
                .build(), Pageable.unpaged());
        assertThat(searchResult.getTotalElements()).isEqualTo(1);
        assertThat(searchResult.getTotalPages()).isEqualTo(1);
        assertThat(searchResult.getSize()).isEqualTo(1);
        assertThat(searchResult.getContent().size()).isEqualTo(1);

        searchResult = userRepository.findAll(UserSearchCriteriaDo.builder()
                .email("email0")
                .build(), Pageable.unpaged());
        assertThat(searchResult.getTotalElements()).isEqualTo(1);
        assertThat(searchResult.getTotalPages()).isEqualTo(1);
        assertThat(searchResult.getSize()).isEqualTo(1);
        assertThat(searchResult.getContent().size()).isEqualTo(1);

        for (final UserDo user : users) {
            userRepository.deleteById(user.getId());
        }

        searchResult = userRepository.findAll(UserSearchCriteriaDo.builder().build(), Pageable.unpaged());
        assertThat(searchResult.getTotalElements()).isEqualTo(0);
        assertThat(searchResult.getTotalPages()).isEqualTo(1);
        assertThat(searchResult.getSize()).isEqualTo(0);
        assertThat(searchResult.getContent().size()).isEqualTo(0);
    }
}
