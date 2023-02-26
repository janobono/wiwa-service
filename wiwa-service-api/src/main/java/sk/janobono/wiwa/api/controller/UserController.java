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
    public ResponseEntity<Page<UserSo>> getUsers(Pageable pageable) {
        log.debug("getUsers({})", pageable);
        return new ResponseEntity<>(userService.getUsers(pageable), HttpStatus.OK);
    }

    @GetMapping("/by-search-criteria")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager', 'w-employee')")
    public ResponseEntity<Page<UserSo>> getUsers(
            @RequestParam(value = "search-field", required = false) String searchField,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "email", required = false) String email,
            Pageable pageable) {
        log.debug("getUsersBySearchCriteria({},{},{},{})", searchField, username, email, pageable);
        return new ResponseEntity<>(userService.getUsers(new UserSearchCriteriaSo(searchField, username, email), pageable), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager', 'w-employee')")
    public ResponseEntity<UserSo> getUser(@PathVariable("id") Long id) {
        log.debug("getUser({})", id);
        return new ResponseEntity<>(userService.getUser(id), HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('w-admin')")
    public ResponseEntity<UserSo> addUser(@Valid @RequestBody UserDataSo userDataDto) {
        log.debug("addUser({})", userDataDto);
        return new ResponseEntity<>(userService.addUser(userDataDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('w-admin')")
    public ResponseEntity<UserSo> setUser(@PathVariable("id") Long id, @Valid @RequestBody UserProfileSo userProfileDto) {
        log.debug("setUser({},{})", id, userProfileDto);
        return new ResponseEntity<>(userService.setUser(id, userProfileDto), HttpStatus.OK);
    }

    @PatchMapping("/{id}/authorities")
    @PreAuthorize("hasAuthority('w-admin')")
    public ResponseEntity<UserSo> setAuthorities(@PathVariable("id") Long id, @Valid @RequestBody Set<Authority> authorities) {
        log.debug("setAuthorities({},{})", id, authorities);
        return new ResponseEntity<>(userService.setAuthorities(id, authorities), HttpStatus.OK);
    }

    @PatchMapping("/{id}/confirm")
    @PreAuthorize("hasAuthority('w-admin')")
    public ResponseEntity<UserSo> setConfirmed(@PathVariable("id") Long id, @Valid @RequestBody Boolean confirmed) {
        log.debug("setConfirmed({},{})", id, confirmed);
        return new ResponseEntity<>(userService.setConfirmed(id, confirmed), HttpStatus.OK);
    }

    @PatchMapping("/{id}/enable")
    @PreAuthorize("hasAuthority('w-admin')")
    public ResponseEntity<UserSo> setEnabled(@PathVariable("id") Long id, @Valid @RequestBody Boolean enabled) {
        log.debug("setEnabled({},{})", id, enabled);
        return new ResponseEntity<>(userService.setEnabled(id, enabled), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('w-admin')")
    public void deleteUser(@PathVariable("id") Long id) {
        log.debug("deleteUser({})", id);
        userService.deleteUser(id);
    }
}
