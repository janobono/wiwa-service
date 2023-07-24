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
        final String locale = request.getParameter("locale");
        if (!Optional.ofNullable(locale).map(String::isBlank).orElse(true)) {
            requestLocale.setLocale(StringUtils.parseLocale(locale));
        }
        filterChain.doFilter(request, response);
    }
}
