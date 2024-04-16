package sk.janobono.wiwa.business.impl.util;

import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.internal.Function;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.dal.domain.ApplicationPropertyDo;
import sk.janobono.wiwa.dal.repository.ApplicationPropertyRepository;
import sk.janobono.wiwa.exception.WiwaException;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PropertyUtilService {

    private final ApplicationPropertyRepository applicationPropertyRepository;

    public String getProperty(final String key) {
        return getPropertyValue(key).orElseThrow(
                () -> WiwaException.APPLICATION_PROPERTY_NOT_FOUND.exception(
                        "Application Property {0} not found", key
                ));
    }

    public <T> T getProperty(final Function<String, T> mapper, final String key) {
        return getPropertyValue(mapper, key).orElseThrow(
                () -> WiwaException.APPLICATION_PROPERTY_NOT_FOUND.exception(
                        "Application Property {0} not found", key
                ));
    }

    public Optional<String> getPropertyValue(final String key) {
        return getPropertyValue(v -> v, key);
    }

    public <T> Optional<T> getPropertyValue(final Function<String, T> mapper, final String key) {
        return applicationPropertyRepository.findByKey(key)
                .map(ApplicationPropertyDo::getValue)
                .map(mapper::apply);
    }

    public void setProperty(final String key, final String value) {
        setProperty(v -> v, key, value);
    }

    public <T> void setProperty(final Function<T, String> mapper, final String key, final T value) {
        applicationPropertyRepository.save(
                ApplicationPropertyDo.builder()
                        .key(key)
                        .value(mapper.apply(value))
                        .build()
        );
    }
}
