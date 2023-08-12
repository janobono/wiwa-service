package sk.janobono.wiwa.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import sk.janobono.wiwa.component.JwtToken;
import sk.janobono.wiwa.model.User;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtToken jwtToken;

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
                    final User user = jwtToken.parseToken(token);
                    final List<SimpleGrantedAuthority> authorities = user.authorities().stream()
                            .map(authority -> new SimpleGrantedAuthority(authority.toString())).collect(Collectors.toList());
                    SecurityContextHolder.getContext()
                            .setAuthentication(new UsernamePasswordAuthenticationToken(user, null, authorities));
                });
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
