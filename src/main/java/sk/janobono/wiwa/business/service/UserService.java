package sk.janobono.wiwa.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.business.model.user.UserDataSo;
import sk.janobono.wiwa.business.model.user.UserProfileSo;
import sk.janobono.wiwa.business.model.user.UserSearchCriteriaSo;
import sk.janobono.wiwa.business.service.util.UserUtilService;
import sk.janobono.wiwa.component.RandomString;
import sk.janobono.wiwa.component.ScDf;
import sk.janobono.wiwa.dal.domain.UserDo;
import sk.janobono.wiwa.dal.repository.AuthorityRepository;
import sk.janobono.wiwa.dal.repository.CodeListItemRepository;
import sk.janobono.wiwa.dal.repository.UserRepository;
import sk.janobono.wiwa.exception.WiwaException;
import sk.janobono.wiwa.model.Authority;
import sk.janobono.wiwa.model.User;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final RandomString randomString;
    private final ScDf scDf;
    private final AuthorityRepository authorityRepository;
    private final UserRepository userRepository;
    private final CodeListItemRepository codeListItemRepository;
    private final UserUtilService userUtilService;

    public Page<User> getUsers(final UserSearchCriteriaSo criteria, final Pageable pageable) {
        return userRepository.findAll(criteria, pageable).map(userUtilService::mapToUser);
    }

    public User getUser(final Long id) {
        return userUtilService.mapToUser(userUtilService.getUserDo(id));
    }

    public User addUser(final UserDataSo data) {
        if (userRepository.existsByUsername(scDf.toStripAndLowerCase(data.username()))) {
            throw WiwaException.USER_USERNAME_IS_USED.exception("Username is used");
        }

        if (userRepository.existsByEmail(scDf.toStripAndLowerCase(data.email()))) {
            throw WiwaException.USER_EMAIL_IS_USED.exception("Email is used");
        }

        final UserDo userDo = userRepository.save(
                UserDo.builder()
                        .username(scDf.toStripAndLowerCase(data.username()))
                        .password(passwordEncoder.encode(randomString.alphaNumeric(3, 2, 1, 6, 6)))
                        .titleBefore(data.titleBefore())
                        .firstName(data.firstName())
                        .midName(data.midName())
                        .lastName(data.lastName())
                        .titleAfter(data.titleAfter())
                        .email(scDf.toStripAndLowerCase(data.email()))
                        .gdpr(data.gdpr())
                        .confirmed(data.confirmed())
                        .enabled(data.enabled()).build()
        );
        authorityRepository.saveUserAuthorities(userDo.getId(), data.authorities());
        return userUtilService.mapToUser(userDo);
    }

    public User setUser(final Long id, final UserProfileSo userProfile) {
        final UserDo userDo = userUtilService.getUserDo(id);

        userDo.setTitleBefore(userProfile.titleBefore());
        userDo.setFirstName(userProfile.firstName());
        userDo.setMidName(userProfile.midName());
        userDo.setLastName(userProfile.lastName());
        userDo.setTitleAfter(userProfile.titleAfter());

        return userUtilService.mapToUser(userRepository.save(userDo));
    }

    public User setAuthorities(final Long id, final List<Authority> authorities) {
        final UserDo userDo = userUtilService.getUserDo(id);

        authorityRepository.saveUserAuthorities(userDo.getId(), authorities);

        return userUtilService.mapToUser(userDo);
    }

    public User setConfirmed(final Long id, final Boolean confirmed) {
        final UserDo userDo = userUtilService.getUserDo(id);

        userDo.setConfirmed(confirmed);

        return userUtilService.mapToUser(userRepository.save(userDo));
    }

    public User setEnabled(final Long id, final Boolean enabled) {
        final UserDo userDo = userUtilService.getUserDo(id);

        userDo.setEnabled(enabled);

        return userUtilService.mapToUser(userRepository.save(userDo));
    }

    public User setUserCodeListItems(final Long id, final List<Long> itemIds) {
        final UserDo userDo = userUtilService.getUserDo(id);

        codeListItemRepository.saveUserCodeListItems(userDo.getId(), itemIds);

        return userUtilService.mapToUser(userDo);
    }

    public void deleteUser(final Long id) {
        if (!userRepository.existsById(id)) {
            throw WiwaException.USER_NOT_FOUND.exception("User with id {0} not found", id);
        }

        userRepository.deleteById(id);
    }
}
