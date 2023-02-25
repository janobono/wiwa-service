package sk.janobono.wiwa.dal.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sk.janobono.wiwa.common.model.Authority;
import sk.janobono.wiwa.dal.domain.UserDo;
import sk.janobono.wiwa.dal.model.UserProfileDo;
import sk.janobono.wiwa.dal.model.UserSearchCriteriaDo;

import java.util.Optional;
import java.util.Set;

public interface UserRepository {

    long count();

    boolean exists(Long id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Page<UserDo> getUsers(Pageable pageable);

    Page<UserDo> getUsers(UserSearchCriteriaDo userSearchCriteriaDo, Pageable pageable);

    Optional<UserDo> getUser(Long id);

    Optional<UserDo> getUserByEmail(String email);

    Optional<UserDo> getUserByUsername(String username);

    Optional<String> getUserPassword(Long id);

    Optional<Boolean> getUserEnabled(Long id);

    UserDo addUser(UserDo userDo);

    UserDo setUser(UserDo userDo);

    UserDo setUserAuthorities(Long id, Set<Authority> authorities);

    UserDo setUserConfirmed(Long id, Boolean confirmed);

    UserDo setUserConfirmedAndAuthorities(Long id, Boolean confirmed, Set<Authority> authorities);

    UserDo setUserEmail(Long id, String email);

    UserDo setUserEnabled(Long id, Boolean enabled);

    UserDo setUserPassword(Long id, String password);

    UserDo setUserProfile(Long id, UserProfileDo userProfileDo);

    UserDo setUserProfileAndGdpr(Long id, UserProfileDo userProfileDo, Boolean gdpr);

    void deleteUser(Long id);
}
