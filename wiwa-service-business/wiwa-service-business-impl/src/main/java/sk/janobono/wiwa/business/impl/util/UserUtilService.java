package sk.janobono.wiwa.business.impl.util;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.dal.domain.AuthorityDo;
import sk.janobono.wiwa.dal.domain.UserDo;
import sk.janobono.wiwa.dal.repository.AuthorityRepository;
import sk.janobono.wiwa.dal.repository.UserRepository;
import sk.janobono.wiwa.exception.WiwaException;
import sk.janobono.wiwa.model.User;

@RequiredArgsConstructor
@Service
public class UserUtilService {

    private final PasswordEncoder passwordEncoder;
    private final AuthorityRepository authorityRepository;
    private final UserRepository userRepository;

    public void checkEnabled(final UserDo userDo) {
        if (!userDo.isEnabled()) {
            throw WiwaException.USER_IS_DISABLED.exception("User is disabled");
        }
    }

    public void checkPassword(final UserDo userDo, final String password) {
        if (!passwordEncoder.matches(password, userDo.getPassword())) {
            throw WiwaException.INVALID_CREDENTIALS.exception("Invalid credentials");
        }
    }

    public UserDo getUserDo(final Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> WiwaException.USER_NOT_FOUND.exception("User with id {0} not found", id));
    }

    public User mapToUser(final UserDo userDo) {
        return User.builder()
                .id(userDo.getId())
                .username(userDo.getUsername())
                .titleBefore(userDo.getTitleBefore())
                .firstName(userDo.getFirstName())
                .midName(userDo.getMidName())
                .lastName(userDo.getLastName())
                .titleAfter(userDo.getTitleAfter())
                .email(userDo.getEmail())
                .gdpr(userDo.isGdpr())
                .confirmed(userDo.isConfirmed())
                .enabled(userDo.isEnabled())
                .authorities(authorityRepository.findByUserId(userDo.getId()).stream().map(AuthorityDo::getAuthority).toList())
                .build();
    }
}
