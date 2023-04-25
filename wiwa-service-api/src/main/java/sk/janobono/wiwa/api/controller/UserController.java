package sk.janobono.wiwa.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sk.janobono.wiwa.api.model.SingleValueBody;
import sk.janobono.wiwa.business.model.user.UserDataSo;
import sk.janobono.wiwa.business.model.user.UserProfileSo;
import sk.janobono.wiwa.business.model.user.UserSearchCriteriaSo;
import sk.janobono.wiwa.business.service.UserService;
import sk.janobono.wiwa.common.model.Authority;
import sk.janobono.wiwa.common.model.UserSo;

import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager', 'w-employee')")
    public Page<UserSo> getUsers(
            @RequestParam(value = "search-field", required = false) final String searchField,
            @RequestParam(value = "username", required = false) final String username,
            @RequestParam(value = "email", required = false) final String email,
            final Pageable pageable
    ) {
        log.debug("getUsers({})", pageable);
        return userService.getUsers(new UserSearchCriteriaSo(searchField, username, email), pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager', 'w-employee')")
    public UserSo getUser(@PathVariable("id") final Long id) {
        log.debug("getUser({})", id);
        return userService.getUser(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('w-admin')")
    @ResponseStatus(HttpStatus.CREATED)
    public UserSo addUser(@Valid @RequestBody final UserDataSo userDataDto) {
        log.debug("addUser({})", userDataDto);
        return userService.addUser(userDataDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('w-admin')")
    public UserSo setUser(@PathVariable("id") final Long id, @Valid @RequestBody final UserProfileSo userProfileDto) {
        log.debug("setUser({},{})", id, userProfileDto);
        return userService.setUser(id, userProfileDto);
    }

    @PatchMapping("/{id}/authorities")
    @PreAuthorize("hasAuthority('w-admin')")
    public UserSo setAuthorities(@PathVariable("id") final Long id, @Valid @RequestBody final SingleValueBody<Set<Authority>> authorities) {
        log.debug("setAuthorities({},{})", id, authorities);
        return userService.setAuthorities(id, authorities.value());
    }

    @PatchMapping("/{id}/confirm")
    @PreAuthorize("hasAuthority('w-admin')")
    public UserSo setConfirmed(@PathVariable("id") final Long id, @Valid @RequestBody final SingleValueBody<Boolean> confirmed) {
        log.debug("setConfirmed({},{})", id, confirmed);
        return userService.setConfirmed(id, confirmed.value());
    }

    @PatchMapping("/{id}/enable")
    @PreAuthorize("hasAuthority('w-admin')")
    public UserSo setEnabled(@PathVariable("id") final Long id, @Valid @RequestBody final SingleValueBody<Boolean> enabled) {
        log.debug("setEnabled({},{})", id, enabled);
        return userService.setEnabled(id, enabled.value());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('w-admin')")
    public void deleteUser(@PathVariable("id") final Long id) {
        log.debug("deleteUser({})", id);
        userService.deleteUser(id);
    }
}
