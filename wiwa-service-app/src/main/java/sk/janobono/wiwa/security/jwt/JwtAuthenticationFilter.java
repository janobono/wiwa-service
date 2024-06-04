package sk.janobono.wiwa.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import sk.janobono.wiwa.business.impl.util.PropertyUtilService;
import sk.janobono.wiwa.business.impl.util.UserUtilService;
import sk.janobono.wiwa.component.JwtToken;
import sk.janobono.wiwa.dal.repository.ApplicationPropertyRepository;
import sk.janobono.wiwa.dal.repository.AuthorityRepository;
import sk.janobono.wiwa.dal.repository.UserRepository;
import sk.janobono.wiwa.model.Authority;
import sk.janobono.wiwa.model.User;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtToken jwtToken;

    private final PropertyUtilService propertyUtilService;
    private final UserUtilService userUtilService;

    public JwtAuthenticationFilter(final JwtToken jwtToken,
                                   final ApplicationPropertyRepository applicationPropertyRepository,
                                   final AuthorityRepository authorityRepository,
                                   final UserRepository userRepository) {
        this.jwtToken = jwtToken;
        propertyUtilService = new PropertyUtilService(applicationPropertyRepository);
        userUtilService = new UserUtilService(null, authorityRepository, userRepository);
    }

    @Override
    protected void doFilterInternal(
            final HttpServletRequest httpServletRequest,
            final HttpServletResponse httpServletResponse,
            final FilterChain filterChain) throws IOException, ServletException {
        Optional.ofNullable(httpServletRequest.getHeader("Authorization"))
                .filter(s -> !s.isBlank())
                .filter(s -> s.startsWith("Bearer "))
                .map(s -> s.replace("Bearer ", ""))
                .ifPresent(token -> {
                    final Long id = jwtToken.parseToken(token);
                    final User user = getUser(id);
                    final boolean maintenance = getMaintenance()
                            && !user.authorities().contains(Authority.W_ADMIN)
                            && !user.authorities().contains(Authority.W_MANAGER);
                    if (!maintenance) {
                        final List<SimpleGrantedAuthority> authorities = user.authorities().stream()
                                .map(authority -> new SimpleGrantedAuthority(authority.toString())).collect(Collectors.toList());
                        SecurityContextHolder.getContext()
                                .setAuthentication(new UsernamePasswordAuthenticationToken(user, null, authorities));
                    }
                });
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private User getUser(final Long id) {
        return userUtilService.mapToUser(userUtilService.getUserDo(id));
    }

    private boolean getMaintenance() {
        return propertyUtilService.getProperty(Boolean::valueOf, "MAINTENANCE").orElse(true);
    }
}
