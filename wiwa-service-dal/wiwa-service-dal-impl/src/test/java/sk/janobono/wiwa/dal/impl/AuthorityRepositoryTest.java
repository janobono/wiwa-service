package sk.janobono.wiwa.dal.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sk.janobono.wiwa.dal.domain.AuthorityDo;
import sk.janobono.wiwa.dal.domain.UserDo;
import sk.janobono.wiwa.dal.repository.AuthorityRepository;
import sk.janobono.wiwa.dal.repository.UserRepository;
import sk.janobono.wiwa.model.Authority;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AuthorityRepositoryTest extends BaseRepositoryTest {

    @Autowired
    public AuthorityRepository authorityRepository;

    @Autowired
    public UserRepository userRepository;

    @Test
    void fullTest() {
        final AuthorityDo saved = authorityRepository.save(AuthorityDo.builder()
                .authority(Authority.W_CUSTOMER)
                .build());
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getAuthority()).isEqualTo(Authority.W_CUSTOMER);

        final int count = authorityRepository.count();
        assertThat(count).isEqualTo(1);

        final List<AuthorityDo> authorities = authorityRepository.findAll();
        assertThat(authorities).hasSize(1);
        assertThat(authorities.getFirst()).usingRecursiveComparison().isEqualTo(saved);

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
        authorityRepository.saveUserAuthorities(user.getId(), List.of(Authority.W_CUSTOMER));

        final List<AuthorityDo> userAuthorities = authorityRepository.findByUserId(user.getId());
        assertThat(userAuthorities).hasSize(1);
        assertThat(userAuthorities.getFirst()).usingRecursiveComparison().isEqualTo(saved);
    }
}
