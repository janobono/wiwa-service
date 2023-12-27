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

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager', 'w-employee')")
    public Page<User> getUsers(
            @RequestParam(value = "searchField", required = false) final String searchField,
            @RequestParam(value = "username", required = false) final String username,
            @RequestParam(value = "email", required = false) final String email,
            @RequestParam(value = "codeListItems", required = false) final List<String> codeListItems,
            final Pageable pageable
    ) {
        final UserSearchCriteriaSo criteria = UserSearchCriteriaSo.builder()
                .searchField(searchField)
                .username(username)
                .email(email)
                .codeListItems(codeListItems)
                .build();
        return userService.getUsers(criteria, pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager', 'w-employee')")
    public User getUser(@PathVariable("id") final Long id) {
        return userService.getUser(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('w-admin')")
    @ResponseStatus(HttpStatus.CREATED)
    public User addUser(@Valid @RequestBody final UserDataSo data) {
        return userService.addUser(data);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('w-admin')")
    public User setUser(@PathVariable("id") final Long id, @Valid @RequestBody final UserProfileSo userProfileSo) {
        return userService.setUser(id, userProfileSo);
    }

    @PatchMapping("/{id}/authorities")
    @PreAuthorize("hasAuthority('w-admin')")
    public User setAuthorities(@PathVariable("id") final Long id, @Valid @RequestBody final List<Authority> authorities) {
        return userService.setAuthorities(id, authorities);
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

    @PatchMapping("/{id}/code-list-items")
    @PreAuthorize("hasAnyAuthority('p2-admin', 'p2-manager')")
    public User setUserCodeListItems(@PathVariable("id") final Long id, @RequestBody final List<Long> itemIds) {
        return userService.setUserCodeListItems(id, itemIds);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('w-admin')")
    public void deleteUser(@PathVariable("id") final Long id) {
        userService.deleteUser(id);
    }
}
