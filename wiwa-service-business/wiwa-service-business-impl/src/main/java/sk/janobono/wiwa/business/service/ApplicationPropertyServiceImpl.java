package sk.janobono.wiwa.business.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.common.exception.WiwaException;
import sk.janobono.wiwa.common.locale.RequestLocale;
import sk.janobono.wiwa.common.model.ApplicationPropertyKey;
import sk.janobono.wiwa.dal.domain.ApplicationPropertyDo;
import sk.janobono.wiwa.dal.repository.ApplicationPropertyRepository;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ApplicationPropertyServiceImpl implements ApplicationPropertyService {

    private final RequestLocale requestLocale;
    private final ApplicationPropertyRepository applicationPropertyRepository;

    public String getProperty(final ApplicationPropertyKey key, final Object... arguments) {
        log.debug("getProperty({},{})", key, arguments);
        final Optional<ApplicationPropertyDo> property = getApplicationProperty(key, arguments);
        final String result = property.map(ApplicationPropertyDo::value).orElseThrow(
                () -> WiwaException.APPLICATION_PROPERTY_NOT_FOUND.exception(
                        "Application Property {0},{1} not found", key.getGroup(), key.getKey(arguments)
                ));
        log.debug("getApplicationProperty({},{})={}", key, arguments, result);
        return result;
    }

    private Optional<ApplicationPropertyDo> getApplicationProperty(final ApplicationPropertyKey key, final Object... arguments) {
        log.debug("getApplicationProperty({},{})", key, arguments);
        final Optional<ApplicationPropertyDo> property;
        if (key.isLocalized()) {
            property = applicationPropertyRepository.getApplicationProperty(
                    key.getGroup(), key.getKey(arguments), requestLocale.getLocale().getLanguage()
            );
        } else {
            property = applicationPropertyRepository.getApplicationProperty(
                    key.getGroup(), key.getKey(arguments), ""
            );
        }
        log.debug("ApplicationProperty({},{})={}", key, arguments, property);
        return property;
    }

    public String setApplicationProperty(final String group, final String key, final String language, final String value) {
        log.debug("setApplicationProperty({},{},{},{})", group, key, language, value);
        final ApplicationPropertyDo applicationPropertyDo;
        if (applicationPropertyRepository.exists(group, key, language)) {
            applicationPropertyDo = applicationPropertyRepository.setApplicationProperty(
                    new ApplicationPropertyDo(group, key, language, value));
        } else {
            applicationPropertyDo = applicationPropertyRepository.addApplicationProperty(
                    new ApplicationPropertyDo(group, key, language, value));
        }
        return applicationPropertyDo.value();
    }
}
