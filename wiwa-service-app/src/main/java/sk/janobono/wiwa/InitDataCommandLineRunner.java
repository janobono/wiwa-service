package sk.janobono.wiwa;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import sk.janobono.wiwa.config.CommonConfigProperties;
import sk.janobono.wiwa.dal.domain.AuthorityDo;
import sk.janobono.wiwa.dal.domain.UserDo;
import sk.janobono.wiwa.dal.repository.AuthorityRepository;
import sk.janobono.wiwa.dal.repository.UserRepository;
import sk.janobono.wiwa.model.Authority;

@RequiredArgsConstructor
@Component
public class InitDataCommandLineRunner implements CommandLineRunner {

    private final CommonConfigProperties commonConfigProperties;
    private final PasswordEncoder passwordEncoder;
    private final AuthorityRepository authorityRepository;
    private final UserRepository userRepository;

    @Override
    public void run(final String... args) {
        initAuthorities();
        initUsers();
    }

    private void initAuthorities() {
        if (authorityRepository.count() == 0L) {
            for (final Authority authority : Authority.values()) {
                authorityRepository.save(AuthorityDo.builder().authority(authority).build());
            }
        }
    }

    private void initUsers() {
        if (userRepository.count() == 0L) {
            final UserDo userDo = userRepository.save(UserDo.builder()
                    .username("wiwa")
                    .password(passwordEncoder.encode("wiwa"))
                    .firstName("wiwa")
                    .lastName("wiwa")
                    .email(commonConfigProperties.mail())
                    .gdpr(true)
                    .confirmed(true)
                    .enabled(true)
                    .build());
            authorityRepository.saveUserAuthorities(userDo.getId(), authorityRepository.findAll().stream().map(AuthorityDo::getAuthority).toList());
        }
    }
}
