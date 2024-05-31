package sk.janobono.wiwa.business;

import org.springframework.security.crypto.password.PasswordEncoder;
import sk.janobono.wiwa.dal.domain.AuthorityDo;
import sk.janobono.wiwa.dal.domain.UserDo;
import sk.janobono.wiwa.dal.repository.AuthorityRepository;
import sk.janobono.wiwa.dal.repository.UserRepository;
import sk.janobono.wiwa.model.Authority;

import java.util.List;

public class TestUsers {

    public static final String DEFAULT_ADMIN = "wiwa";
    public static final String DEFAULT_MANAGER = "wmanager";
    public static final String DEFAULT_EMPLOYEE = "wemployee";
    public static final String DEFAULT_CUSTOMER = "wcustomer";
    public static final String PASSWORD = "wiwa";

    public void initUsers(final PasswordEncoder passwordEncoder,
                          final UserRepository userRepository,
                          final AuthorityRepository authorityRepository
    ) {
        if (authorityRepository.count() == 0) {
            for (final Authority authority : Authority.values()) {
                authorityRepository.save(AuthorityDo.builder().authority(authority).build());
            }
        }
        if (userRepository.count() == 0) {
            UserDo user = addUser(passwordEncoder, userRepository, DEFAULT_ADMIN);
            authorityRepository.saveUserAuthorities(user.getId(), List.of(Authority.W_ADMIN));
            user = addUser(passwordEncoder, userRepository, DEFAULT_MANAGER);
            authorityRepository.saveUserAuthorities(user.getId(), List.of(Authority.W_MANAGER));
            user = addUser(passwordEncoder, userRepository, DEFAULT_EMPLOYEE);
            authorityRepository.saveUserAuthorities(user.getId(), List.of(Authority.W_EMPLOYEE));
            user = addUser(passwordEncoder, userRepository, DEFAULT_CUSTOMER);
            authorityRepository.saveUserAuthorities(user.getId(), List.of(Authority.W_CUSTOMER));
        }
    }

    private UserDo addUser(final PasswordEncoder passwordEncoder, final UserRepository userRepository, final String username) {
        final UserDo user = UserDo.builder()
                .username(username)
                .password(passwordEncoder.encode(PASSWORD))
                .firstName("test")
                .lastName("test")
                .email(username + "@test.com")
                .gdpr(true)
                .confirmed(true)
                .enabled(true)
                .build();
        return userRepository.save(user);
    }
}
