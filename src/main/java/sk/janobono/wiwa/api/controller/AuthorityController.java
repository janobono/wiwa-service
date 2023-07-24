package sk.janobono.wiwa.api.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sk.janobono.wiwa.model.Authority;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/authorities")
public class AuthorityController {

    @GetMapping
    @PreAuthorize("hasAnyAuthority('w-admin')")
    public Set<Authority> getAuthorities() {
        return Arrays.stream(Authority.values()).collect(Collectors.toSet());
    }
}
