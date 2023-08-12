package sk.janobono.wiwa.locale;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Component
@Order(2)
public class LocaleFilter extends OncePerRequestFilter {

    private final RequestLocale requestLocale;

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        Optional.ofNullable(request.getParameter("locale"))
                .filter(s -> !s.isBlank())
                .map(StringUtils::parseLocale)
                .ifPresent(requestLocale::setLocale);
        filterChain.doFilter(request, response);
    }
}
