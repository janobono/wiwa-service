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

    Page<User> getUsers(UserSearchCriteriaData criteria, Pageable pageable);

    User getUser(Long id);

    User addUser(UserCreateData data);

    User setUser(Long id, UserProfileData userProfile);

    User setAuthorities(Long id, List<Authority> authorities);

    User setConfirmed(Long id, Boolean confirmed);

    User setEnabled(Long id, Boolean enabled);

    void deleteUser(Long id);
}
