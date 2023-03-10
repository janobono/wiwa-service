package sk.janobono.wiwa.business.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import sk.janobono.wiwa.business.mapper.UserMapper;
import sk.janobono.wiwa.business.model.user.UserDataSo;
import sk.janobono.wiwa.business.model.user.UserProfileSo;
import sk.janobono.wiwa.business.model.user.UserSearchCriteriaSo;
import sk.janobono.wiwa.common.component.RandomString;
import sk.janobono.wiwa.common.component.ScDf;
import sk.janobono.wiwa.common.exception.WiwaException;
import sk.janobono.wiwa.common.model.Authority;
import sk.janobono.wiwa.common.model.UserSo;
import sk.janobono.wiwa.dal.domain.UserDo;
import sk.janobono.wiwa.dal.model.UserProfileDo;
import sk.janobono.wiwa.dal.model.UserSearchCriteriaDo;
import sk.janobono.wiwa.dal.repository.UserRepository;

import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final RandomString randomString;
    private final ScDf scDf;
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public Page<UserSo> getUsers(Pageable pageable) {
        log.debug("getUsers({})", pageable);
        Page<UserSo> result = userRepository.getUsers(pageable).map(userMapper::mapToSo);
        log.debug("getUsers({})={}", pageable, result);
        return result;
    }

    public Page<UserSo> getUsers(UserSearchCriteriaSo userSearchCriteriaSo, Pageable pageable) {
        log.debug("getUsers({},{})", userSearchCriteriaSo, pageable);
        Page<UserSo> result = userRepository.getUsers(new UserSearchCriteriaDo(
                        scDf.toScDf(userSearchCriteriaSo.searchField()),
                        scDf.toScDf(userSearchCriteriaSo.username()),
                        scDf.toScDf(userSearchCriteriaSo.email())
                ), pageable)
                .map(userMapper::mapToSo);
        log.debug("getUsers({},{})={}", userSearchCriteriaSo, pageable, result);
        return result;
    }

    public UserSo getUser(Long id) {
        log.debug("getUser({})", id);
        UserDo userDo = userRepository.getUser(id).orElseThrow(
                () -> WiwaException.USER_NOT_FOUND.exception("User with id {0} not found", id)
        );
        UserSo userSo = userMapper.mapToSo(userDo);
        log.debug("getUser({})={}", id, userSo);
        return userSo;
    }

    public UserSo addUser(UserDataSo userDataSo) {
        log.debug("addUser({})", userDataSo);
        if (userRepository.existsByUsername(stripAndLowerCase(userDataSo.username()))) {
            throw WiwaException.USER_USERNAME_IS_USED.exception("Username is used");
        }
        if (userRepository.existsByEmail(stripAndLowerCase(userDataSo.email()))) {
            throw WiwaException.USER_EMAIL_IS_USED.exception("Email is used");
        }
        UserDo userDo = new UserDo(
                null,
                stripAndLowerCase(userDataSo.username()),
                passwordEncoder.encode(randomString.alphaNumeric(3, 2, 1, 6, 6)),
                userDataSo.titleBefore(),
                userDataSo.firstName(),
                userDataSo.midName(),
                userDataSo.lastName(),
                userDataSo.titleAfter(),
                stripAndLowerCase(userDataSo.email()),
                userDataSo.gdpr(),
                userDataSo.confirmed(),
                userDataSo.enabled(),
                userDataSo.authorities()
        );
        userDo = userRepository.addUser(userDo);
        UserSo result = userMapper.mapToSo(userDo);
        log.debug("addUser({})={}", userDataSo, result);
        return result;
    }

    public UserSo setUser(Long id, UserProfileSo userProfileDto) {
        log.debug("setUser({},{})", id, userProfileDto);
        checkExists(id);
        UserSo result = userMapper.mapToSo(userRepository.setUserProfile(id, new UserProfileDo(
                userProfileDto.titleBefore(),
                userProfileDto.firstName(),
                userProfileDto.midName(),
                userProfileDto.lastName(),
                userProfileDto.titleAfter()
        )));
        log.debug("setUser({},{})={}", id, userProfileDto, result);
        return result;
    }

    public UserSo setAuthorities(Long id, Set<Authority> authorities) {
        log.debug("setAuthorities({},{})", id, authorities);
        checkExists(id);
        UserSo result = userMapper.mapToSo(userRepository.setUserAuthorities(id, authorities));
        log.debug("setAuthorities({},{})={}", id, authorities, result);
        return result;
    }

    public UserSo setConfirmed(Long id, Boolean confirmed) {
        log.debug("setConfirmed({},{})", id, confirmed);
        checkExists(id);
        UserSo result = userMapper.mapToSo(userRepository.setUserConfirmed(id, confirmed));
        log.debug("setConfirmed({},{})={}", id, confirmed, result);
        return result;
    }

    public UserSo setEnabled(Long id, Boolean enabled) {
        log.debug("setEnabled({},{})", id, enabled);
        checkExists(id);
        UserSo result = userMapper.mapToSo(userRepository.setUserEnabled(id, enabled));
        log.debug("setEnabled({},{})={}", id, enabled, result);
        return result;
    }

    public void deleteUser(Long id) {
        log.debug("deleteUser({})", id);
        checkExists(id);
        userRepository.deleteUser(id);
    }

    private String stripAndLowerCase(String s) {
        if (StringUtils.hasLength(s)) {
            return s.strip().toLowerCase();
        }
        return s;
    }

    private void checkExists(Long id) {
        if (!userRepository.exists(id)) {
            throw WiwaException.USER_NOT_FOUND.exception("User with id {0} not found", id);
        }
    }
}
