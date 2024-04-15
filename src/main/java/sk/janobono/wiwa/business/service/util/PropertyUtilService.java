package sk.janobono.wiwa.business.service.util;

import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.internal.Function;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.dal.domain.ApplicationPropertyDo;
import sk.janobono.wiwa.dal.repository.ApplicationPropertyRepository;
import sk.janobono.wiwa.exception.WiwaException;
import sk.janobono.wiwa.model.ApplicationPropertyKey;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PropertyUtilService {

    private final ApplicationPropertyRepository applicationPropertyRepository;

    public String getProperty(final ApplicationPropertyKey key, final Object... arguments) {
        return getPropertyValue(v -> v, key, arguments).orElseThrow(
                () -> WiwaException.APPLICATION_PROPERTY_NOT_FOUND.exception(
                        "Application Property {0},{1} not found", key.getGroup(), key.getKey(arguments)
                ));
    }

    public <T> T getProperty(final Function<String, T> mapper, final ApplicationPropertyKey key, final Object... arguments) {
        return getPropertyValue(mapper, key, arguments).orElseThrow(
                () -> WiwaException.APPLICATION_PROPERTY_NOT_FOUND.exception(
                        "Application Property {0},{1} not found", key.getGroup(), key.getKey(arguments)
                ));
    }

    public <T> Optional<T> getPropertyValue(final Function<String, T> mapper, final ApplicationPropertyKey key, final Object... arguments) {
        return getApplicationProperty(key, arguments)
                .map(ApplicationPropertyDo::getValue)
                .map(mapper::apply);
    }

    public <T> T getProperties(final Function<Map<String, String>, T> mapper, final String group) {
        return getPropertiesValue(mapper, group).orElseThrow(
                () -> WiwaException.APPLICATION_PROPERTY_NOT_FOUND.exception(
                        "Application Properties {0} not found", group
                ));
    }

    public <T> Optional<T> getPropertiesValue(final Function<Map<String, String>, T> mapper, final String group) {
        return Optional.ofNullable(mapper.apply(
                applicationPropertyRepository.findByGroup(group).stream()
                        .collect(Collectors.toMap(ApplicationPropertyDo::getKey, ApplicationPropertyDo::getValue))
        ));
    }

    public void setProperty(final String group, final String key, final String value) {
        setProperty(v -> v, group, key, value);
    }

    public <T> void setProperty(final Function<T, String> mapper, final String group, final String key, final T value) {
        applicationPropertyRepository.save(
                ApplicationPropertyDo.builder()
                        .group(group)
                        .key(key)
                        .value(mapper.apply(value))
                        .build()
        );
    }

    public <T> void setProperties(final Function<T, Map<String, String>> mapper, final String group, final T value) {
        final Map<String, String> map = mapper.apply(value);
        map.forEach((key, value1) -> applicationPropertyRepository.save(
                ApplicationPropertyDo.builder()
                        .group(group)
                        .key(key)
                        .value(value1)
                        .build()
        ));
    }

    private Optional<ApplicationPropertyDo> getApplicationProperty(final ApplicationPropertyKey key, final Object... arguments) {
        return applicationPropertyRepository.findByGroupAndKey(key.getGroup(), key.getKey(arguments));
    }
}
