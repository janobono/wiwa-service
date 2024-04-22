package sk.janobono.wiwa.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.api.mapper.UserWebMapper;
import sk.janobono.wiwa.api.model.user.UserCreateWebDto;
import sk.janobono.wiwa.api.model.user.UserProfileWebDto;
import sk.janobono.wiwa.api.model.user.UserWebDto;
import sk.janobono.wiwa.business.model.user.UserSearchCriteriaData;
import sk.janobono.wiwa.business.service.UserService;
import sk.janobono.wiwa.model.Authority;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserApiService {

    private final UserService userService;
    private final UserWebMapper userWebMapper;

    public Page<UserWebDto> getUsers(final String searchField, final String username, final String email, final Pageable pageable) {
        final UserSearchCriteriaData criteria = UserSearchCriteriaData.builder()
                .searchField(searchField)
                .username(username)
                .email(email)
                .build();
        return userService.getUsers(criteria, pageable).map(userWebMapper::mapToWebDto);
    }

    public UserWebDto getUser(final long id) {
        return userWebMapper.mapToWebDto(userService.getUser(id));
    }

    public UserWebDto addUser(final UserCreateWebDto userCreate) {
        return userWebMapper.mapToWebDto(userService.addUser(userWebMapper.mapToData(userCreate)));
    }

    public UserWebDto setUser(final long id, final UserProfileWebDto userProfile) {
        return userWebMapper.mapToWebDto(userService.setUser(id, userWebMapper.mapToData(userProfile)));
    }

    public UserWebDto setAuthorities(final long id, final List<Authority> authorities) {
        return userWebMapper.mapToWebDto(userService.setAuthorities(id, authorities));
    }

    public UserWebDto setConfirmed(final long id, final boolean value) {
        return userWebMapper.mapToWebDto(userService.setConfirmed(id, value));
    }

    public UserWebDto setEnabled(final long id, final boolean value) {
        return userWebMapper.mapToWebDto(userService.setEnabled(id, value));
    }

    public void deleteUser(final long id) {
        userService.deleteUser(id);
    }
}
