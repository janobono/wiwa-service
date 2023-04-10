package sk.janobono.wiwa.business.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sk.janobono.wiwa.business.model.user.UserDataSo;
import sk.janobono.wiwa.business.model.user.UserProfileSo;
import sk.janobono.wiwa.business.model.user.UserSearchCriteriaSo;
import sk.janobono.wiwa.common.model.Authority;
import sk.janobono.wiwa.common.model.UserSo;

import java.util.Set;

public interface UserService {

    Page<UserSo> getUsers(UserSearchCriteriaSo userSearchCriteriaSo, Pageable pageable);

    UserSo getUser(Long id);

    UserSo addUser(UserDataSo userDataSo);

    UserSo setUser(Long id, UserProfileSo userProfileDto);

    UserSo setAuthorities(Long id, Set<Authority> authorities);

    UserSo setConfirmed(Long id, Boolean confirmed);

    UserSo setEnabled(Long id, Boolean enabled);

    void deleteUser(Long id);
}
