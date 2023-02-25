package sk.janobono.wiwa.dal.domain;

import sk.janobono.wiwa.common.model.Authority;

import java.util.Set;

public record UserDo(
        Long id,
        String username,
        String password,
        String titleBefore,
        String firstName,
        String midName,
        String lastName,
        String titleAfter,
        String email,
        Boolean gdpr,
        Boolean confirmed,
        Boolean enabled,
        Set<Authority> authorities) {
    @Override
    public String toString() {
        return "UserDo{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", titleBefore='" + titleBefore + '\'' +
                ", firstName='" + firstName + '\'' +
                ", midName='" + midName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", titleAfter='" + titleAfter + '\'' +
                ", email='" + email + '\'' +
                ", gdpr=" + gdpr +
                ", confirmed=" + confirmed +
                ", enabled=" + enabled +
                ", authorities=" + authorities +
                '}';
    }
}
