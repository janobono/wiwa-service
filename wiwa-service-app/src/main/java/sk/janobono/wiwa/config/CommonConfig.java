package sk.janobono.wiwa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Configuration
public class CommonConfig {

    @Bean
    public LocaleResolver localeResolver(final CommonConfigProperties commonConfigProperties) {
        final SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();
        sessionLocaleResolver.setDefaultLocale(StringUtils.parseLocale(commonConfigProperties.defaultLocale()));
        return sessionLocaleResolver;
    }
}
