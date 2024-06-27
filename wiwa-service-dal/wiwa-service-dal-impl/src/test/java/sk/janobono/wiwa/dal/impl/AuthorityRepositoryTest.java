package sk.janobono.wiwa.dal.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sk.janobono.wiwa.dal.domain.AuthorityDo;
import sk.janobono.wiwa.dal.domain.UserDo;
import sk.janobono.wiwa.dal.repository.AuthorityRepository;
import sk.janobono.wiwa.dal.repository.UserRepository;
import sk.janobono.wiwa.model.Authority;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class AuthorityRepositoryTest extends BaseRepositoryTest {

    @Autowired
    public AuthorityRepository authorityRepository;

    @Autowired
    public UserRepository userRepository;

    @Test
    void fullTest() {
        for (final Authority authority: Authority.values()) {
            final AuthorityDo saved = authorityRepository.save(AuthorityDo.builder()
                    .authority(authority)
                    .build());
            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getAuthority()).isEqualTo(authority);
        }

        final int count = authorityRepository.count();
        assertThat(count).isEqualTo(4);

        final List<AuthorityDo> authorities = authorityRepository.findAll();
        assertThat(authorities).hasSize(4);

        final UserDo user = userRepository.save(UserDo.builder()
                .username("username")
                .password("password")
                .firstName("firstName")
                .lastName("lastName")
                .email("test@test.ts")
                .gdpr(true)
                .confirmed(true)
                .enabled(true)
                .build()
        );
        authorityRepository.saveUserAuthorities(user.getId(), List.of());
        authorityRepository.saveUserAuthorities(user.getId(), Stream.of(Authority.values()).toList());
        authorityRepository.saveUserAuthorities(user.getId(), List.of(Authority.W_CUSTOMER));
        authorityRepository.saveUserAuthorities(user.getId(), List.of(Authority.W_EMPLOYEE));

        final List<AuthorityDo> userAuthorities = authorityRepository.findByUserId(user.getId());
        assertThat(userAuthorities).hasSize(1);
        assertThat(userAuthorities.getFirst().getAuthority()).usingRecursiveComparison().isEqualTo(Authority.W_EMPLOYEE);
    }
}
