package sk.janobono.wiwa.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sk.janobono.wiwa.business.model.user.UserDataSo;
import sk.janobono.wiwa.business.model.user.UserProfileSo;
import sk.janobono.wiwa.business.model.user.UserSearchCriteriaSo;
import sk.janobono.wiwa.component.RandomString;
import sk.janobono.wiwa.component.ScDf;
import sk.janobono.wiwa.dal.domain.AuthorityDo;
import sk.janobono.wiwa.dal.domain.UserDo;
import sk.janobono.wiwa.dal.model.UserSearchCriteriaDo;
import sk.janobono.wiwa.dal.repository.AuthorityRepository;
import sk.janobono.wiwa.dal.repository.UserRepository;
import sk.janobono.wiwa.exception.WiwaException;
import sk.janobono.wiwa.mapper.UserMapper;
import sk.janobono.wiwa.model.Authority;
import sk.janobono.wiwa.model.User;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final RandomString randomString;
    private final ScDf scDf;
    private final UserMapper userMapper;
    private final AuthorityRepository authorityRepository;
    private final UserRepository userRepository;

    public Page<User> getUsers(final UserSearchCriteriaSo criteria, final Pageable pageable) {
        return userRepository.findAll(new UserSearchCriteriaDo(scDf, criteria), pageable)
                .map(userMapper::mapToUser);
    }

    public User getUser(final Long id) {
        return userRepository.findById(id)
                .map(userMapper::mapToUser)
                .orElseThrow(() -> WiwaException.USER_NOT_FOUND.exception("User with id {0} not found", id));
    }

    @Transactional
    public User addUser(final UserDataSo userData) {
        if (userRepository.existsByUsername(scDf.toStripAndLowerCase(userData.username()))) {
            throw WiwaException.USER_USERNAME_IS_USED.exception("Username is used");
        }
        if (userRepository.existsByEmail(scDf.toStripAndLowerCase(userData.email()))) {
            throw WiwaException.USER_EMAIL_IS_USED.exception("Email is used");
        }

        final UserDo userDo = new UserDo();
        userDo.setUsername(scDf.toStripAndLowerCase(userData.username()));
        userDo.setPassword(passwordEncoder.encode(randomString.alphaNumeric(3, 2, 1, 6, 6)));
        userDo.setTitleBefore(userData.titleBefore());
        userDo.setFirstName(userData.firstName());
        userDo.setMidName(userData.midName());
        userDo.setLastName(userData.lastName());
        userDo.setTitleAfter(userData.titleAfter());
        userDo.setEmail(scDf.toStripAndLowerCase(userData.email()));
        userDo.setGdpr(userData.gdpr());
        userDo.setConfirmed(userData.confirmed());
        userDo.setEnabled(userData.enabled());
        userDo.setAuthorities(mapAuthorities(userData.authorities()));

        return userMapper.mapToUser(userRepository.save(userDo));
    }

    @Transactional
    public User setUser(final Long id, final UserProfileSo userProfile) {
        final UserDo userDo = userRepository.findById(id)
                .orElseThrow(() -> WiwaException.USER_NOT_FOUND.exception("User with id {0} not found", id));

        userDo.setTitleBefore(userProfile.titleBefore());
        userDo.setFirstName(userProfile.firstName());
        userDo.setMidName(userProfile.midName());
        userDo.setLastName(userProfile.lastName());
        userDo.setTitleAfter(userProfile.titleAfter());

        return userMapper.mapToUser(userRepository.save(userDo));
    }

    @Transactional
    public User setAuthorities(final Long id, final Set<Authority> authorities) {
        final UserDo userDo = userRepository.findById(id)
                .orElseThrow(() -> WiwaException.USER_NOT_FOUND.exception("User with id {0} not found", id));

        userDo.setAuthorities(mapAuthorities(authorities));

        return userMapper.mapToUser(userRepository.save(userDo));
    }

    @Transactional
    public User setConfirmed(final Long id, final Boolean confirmed) {
        final UserDo userDo = userRepository.findById(id)
                .orElseThrow(() -> WiwaException.USER_NOT_FOUND.exception("User with id {0} not found", id));

        userDo.setConfirmed(confirmed);

        return userMapper.mapToUser(userRepository.save(userDo));
    }

    @Transactional
    public User setEnabled(final Long id, final Boolean enabled) {
        final UserDo userDo = userRepository.findById(id)
                .orElseThrow(() -> WiwaException.USER_NOT_FOUND.exception("User with id {0} not found", id));

        userDo.setEnabled(enabled);

        return userMapper.mapToUser(userRepository.save(userDo));
    }

    @Transactional
    public void deleteUser(final Long id) {
        if (!userRepository.existsById(id)) {
            throw WiwaException.USER_NOT_FOUND.exception("User with id {0} not found", id);
        }

        userRepository.deleteById(id);
    }

    private Set<AuthorityDo> mapAuthorities(final Set<Authority> authorities) {
        final Set<AuthorityDo> result = new HashSet<>();
        for (final Authority authority : authorities) {
            result.add(authorityRepository.findByAuthority(authority)
                    .orElseThrow(() -> WiwaException.AUTHORITY_NOT_FOUND.exception(authority.name())));
        }
        return result;
    }
}
