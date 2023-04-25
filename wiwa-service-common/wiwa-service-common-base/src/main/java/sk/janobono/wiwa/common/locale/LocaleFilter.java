package sk.janobono.wiwa.common.locale;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
@Component
@Order(2)
public class LocaleFilter extends OncePerRequestFilter {

    private final RequestLocale requestLocale;

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        final String locale = request.getParameter("locale");
        if (StringUtils.hasLength(locale)) {
            requestLocale.setLocale(StringUtils.parseLocale(locale));
        }
        filterChain.doFilter(request, response);
    }
}
