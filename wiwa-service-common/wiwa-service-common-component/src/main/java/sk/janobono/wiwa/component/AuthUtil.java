package sk.janobono.wiwa.component;

import org.springframework.stereotype.Component;
import sk.janobono.wiwa.model.Authority;
import sk.janobono.wiwa.model.User;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

@Component
public class AuthUtil {

    public boolean hasAnyAuthority(final User user, final Authority... authorities) {
        return Optional.of(user).stream()
                .map(User::authorities)
                .flatMap(Collection::stream)
                .anyMatch(userAuthority -> Arrays.asList(authorities).contains(userAuthority));
    }
}
