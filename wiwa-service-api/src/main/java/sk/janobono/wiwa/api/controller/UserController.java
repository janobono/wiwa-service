package sk.janobono.wiwa.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
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
    public Page<UserSo> getUsers(Pageable pageable) {
        log.debug("getUsers({})", pageable);
        return userService.getUsers(pageable);
    }

    @GetMapping("/by-search-criteria")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager', 'w-employee')")
    public Page<UserSo> getUsers(
            @RequestParam(value = "search-field", required = false) String searchField,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "email", required = false) String email,
            Pageable pageable) {
        log.debug("getUsersBySearchCriteria({},{},{},{})", searchField, username, email, pageable);
        return userService.getUsers(new UserSearchCriteriaSo(searchField, username, email), pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager', 'w-employee')")
    public UserSo getUser(@PathVariable("id") Long id) {
        log.debug("getUser({})", id);
        return userService.getUser(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('w-admin')")
    public UserSo addUser(@Valid @RequestBody UserDataSo userDataDto) {
        log.debug("addUser({})", userDataDto);
        return userService.addUser(userDataDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('w-admin')")
    public ResponseEntity<UserSo> setUser(@PathVariable("id") Long id, @Valid @RequestBody UserProfileSo userProfileDto) {
        log.debug("setUser({},{})", id, userProfileDto);
        return new ResponseEntity<>(userService.setUser(id, userProfileDto), HttpStatus.OK);
    }

    @PatchMapping("/{id}/authorities")
    @PreAuthorize("hasAuthority('w-admin')")
    public UserSo setAuthorities(@PathVariable("id") Long id, @Valid @RequestBody Set<Authority> authorities) {
        log.debug("setAuthorities({},{})", id, authorities);
        return userService.setAuthorities(id, authorities);
    }

    @PatchMapping("/{id}/confirm")
    @PreAuthorize("hasAuthority('w-admin')")
    public UserSo setConfirmed(@PathVariable("id") Long id, @Valid @RequestBody Boolean confirmed) {
        log.debug("setConfirmed({},{})", id, confirmed);
        return userService.setConfirmed(id, confirmed);
    }

    @PatchMapping("/{id}/enable")
    @PreAuthorize("hasAuthority('w-admin')")
    public UserSo setEnabled(@PathVariable("id") Long id, @Valid @RequestBody Boolean enabled) {
        log.debug("setEnabled({},{})", id, enabled);
        return userService.setEnabled(id, enabled);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('w-admin')")
    public void deleteUser(@PathVariable("id") Long id) {
        log.debug("deleteUser({})", id);
        userService.deleteUser(id);
    }
}
