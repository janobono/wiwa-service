package sk.janobono.wiwa.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sk.janobono.wiwa.api.model.SingleValueBodyWebDto;
import sk.janobono.wiwa.api.model.user.UserCreateWebDto;
import sk.janobono.wiwa.api.model.user.UserProfileWebDto;
import sk.janobono.wiwa.api.model.user.UserWebDto;
import sk.janobono.wiwa.api.service.UserApiService;
import sk.janobono.wiwa.model.Authority;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserApiService userApiService;

    @Operation(parameters = {
            @Parameter(in = ParameterIn.QUERY, name = "page", content = @Content(schema = @Schema(type = "integer"))),
            @Parameter(in = ParameterIn.QUERY, name = "size", content = @Content(schema = @Schema(type = "integer"))),
            @Parameter(in = ParameterIn.QUERY, name = "sort",
                    content = @Content(array = @ArraySchema(schema = @Schema(type = "string")))
            )
    })
    @GetMapping
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager', 'w-employee')")
    public Page<UserWebDto> getUsers(
            @RequestParam(value = "searchField", required = false) final String searchField,
            @RequestParam(value = "username", required = false) final String username,
            @RequestParam(value = "email", required = false) final String email,
            final Pageable pageable
    ) {
        return userApiService.getUsers(searchField, username, email, pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager', 'w-employee')")
    public UserWebDto getUser(@PathVariable("id") final long id) {
        return userApiService.getUser(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('w-admin')")
    @ResponseStatus(HttpStatus.CREATED)
    public UserWebDto addUser(@Valid @RequestBody final UserCreateWebDto userCreate) {
        return userApiService.addUser(userCreate);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('w-admin')")
    public UserWebDto setUser(@PathVariable("id") final long id, @Valid @RequestBody final UserProfileWebDto userProfile) {
        return userApiService.setUser(id, userProfile);
    }

    @PatchMapping("/{id}/authorities")
    @PreAuthorize("hasAuthority('w-admin')")
    public UserWebDto setAuthorities(@PathVariable("id") final long id, @Valid @RequestBody final List<Authority> authorities) {
        return userApiService.setAuthorities(id, authorities);
    }

    @PatchMapping("/{id}/confirm")
    @PreAuthorize("hasAuthority('w-admin')")
    public UserWebDto setConfirmed(@PathVariable("id") final long id, @Valid @RequestBody final SingleValueBodyWebDto<Boolean> confirmed) {
        return userApiService.setConfirmed(id, confirmed.value());
    }

    @PatchMapping("/{id}/enable")
    @PreAuthorize("hasAuthority('w-admin')")
    public UserWebDto setEnabled(@PathVariable("id") final long id, @Valid @RequestBody final SingleValueBodyWebDto<Boolean> enabled) {
        return userApiService.setEnabled(id, enabled.value());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('w-admin')")
    public void deleteUser(@PathVariable("id") final long id) {
        userApiService.deleteUser(id);
    }
}
