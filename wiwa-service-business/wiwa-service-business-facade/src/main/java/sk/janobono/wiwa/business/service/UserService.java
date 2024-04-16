package sk.janobono.wiwa.business.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sk.janobono.wiwa.business.model.user.UserCreateData;
import sk.janobono.wiwa.business.model.user.UserProfileData;
import sk.janobono.wiwa.business.model.user.UserSearchCriteriaData;
import sk.janobono.wiwa.model.Authority;
import sk.janobono.wiwa.model.User;

import java.util.List;

public interface UserService {

    Page<User> getUsers(final UserSearchCriteriaData criteria, final Pageable pageable);

    User getUser(final Long id);

    User addUser(final UserCreateData data);

    User setUser(final Long id, final UserProfileData userProfile);

    User setAuthorities(final Long id, final List<Authority> authorities);

    User setConfirmed(final Long id, final Boolean confirmed);

    User setEnabled(final Long id, final Boolean enabled);

    void deleteUser(final Long id);
}
