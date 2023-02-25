package sk.janobono.wiwa.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import sk.janobono.wiwa.common.component.JwtToken;
import sk.janobono.wiwa.common.model.UserSo;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtToken jwtToken;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws IOException, ServletException {
        log.debug("doFilterInternal({})", httpServletRequest.getRequestURI());
        String authorizationHeader = httpServletRequest.getHeader("Authorization");
        if (StringUtils.hasLength(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.replace("Bearer ", "");
            UserSo userSo = jwtToken.parseToken(token);
            if (userSo != null) {
                log.debug("user: {}", userSo);
                List<SimpleGrantedAuthority> authorities = userSo.authorities().stream()
                        .map(authority -> new SimpleGrantedAuthority(authority.toString())).collect(Collectors.toList());
                SecurityContextHolder.getContext()
                        .setAuthentication(new UsernamePasswordAuthenticationToken(userSo, null, authorities));
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
