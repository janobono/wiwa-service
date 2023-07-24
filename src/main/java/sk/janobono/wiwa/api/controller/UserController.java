package sk.janobono.wiwa.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import sk.janobono.wiwa.model.Authority;
import sk.janobono.wiwa.model.User;

import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager', 'w-employee')")
    public Page<User> getUsers(
            @RequestParam(value = "search-field", required = false) final String searchField,
            @RequestParam(value = "username", required = false) final String username,
            @RequestParam(value = "email", required = false) final String email,
            final Pageable pageable
    ) {
        return userService.getUsers(new UserSearchCriteriaSo(searchField, username, email), pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager', 'w-employee')")
    public User getUser(@PathVariable("id") final Long id) {
        return userService.getUser(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('w-admin')")
    @ResponseStatus(HttpStatus.CREATED)
    public User addUser(@Valid @RequestBody final UserDataSo userDataSo) {
        return userService.addUser(userDataSo);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('w-admin')")
    public User setUser(@PathVariable("id") final Long id, @Valid @RequestBody final UserProfileSo userProfileSo) {
        return userService.setUser(id, userProfileSo);
    }

    @PatchMapping("/{id}/authorities")
    @PreAuthorize("hasAuthority('w-admin')")
    public User setAuthorities(@PathVariable("id") final Long id, @Valid @RequestBody final SingleValueBody<Set<Authority>> authorities) {
        return userService.setAuthorities(id, authorities.value());
    }

    @PatchMapping("/{id}/confirm")
    @PreAuthorize("hasAuthority('w-admin')")
    public User setConfirmed(@PathVariable("id") final Long id, @Valid @RequestBody final SingleValueBody<Boolean> confirmed) {
        return userService.setConfirmed(id, confirmed.value());
    }

    @PatchMapping("/{id}/enable")
    @PreAuthorize("hasAuthority('w-admin')")
    public User setEnabled(@PathVariable("id") final Long id, @Valid @RequestBody final SingleValueBody<Boolean> enabled) {
        return userService.setEnabled(id, enabled.value());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('w-admin')")
    public void deleteUser(@PathVariable("id") final Long id) {
        userService.deleteUser(id);
    }
}
