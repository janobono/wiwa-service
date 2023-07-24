package sk.janobono.wiwa.locale;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.annotation.RequestScope;
import sk.janobono.wiwa.config.CommonConfigProperties;

import java.util.Locale;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Component
@RequestScope
public class RequestLocale {

    private final CommonConfigProperties commonConfigProperties;

    private Locale locale;

    public Locale getLocale() {
        if (Optional.ofNullable(locale).isEmpty()) {
            log.warn("Default locale used");
            locale = StringUtils.parseLocale(commonConfigProperties.defaultLocale());
        }
        return locale;
    }

    public void setLocale(final Locale locale) {
        this.locale = locale;
    }
}
