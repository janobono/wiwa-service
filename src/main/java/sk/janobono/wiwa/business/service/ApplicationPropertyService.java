package sk.janobono.wiwa.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.dal.domain.ApplicationPropertyDo;
import sk.janobono.wiwa.dal.repository.ApplicationPropertyRepository;
import sk.janobono.wiwa.exception.WiwaException;
import sk.janobono.wiwa.model.ApplicationPropertyKey;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ApplicationPropertyService {
    private final ApplicationPropertyRepository applicationPropertyRepository;

    public String getProperty(final ApplicationPropertyKey key, final Object... arguments) {
        return getPropertyValue(key, arguments).orElseThrow(
                () -> WiwaException.APPLICATION_PROPERTY_NOT_FOUND.exception(
                        "Application Property {0},{1} not found", key.getGroup(), key.getKey(arguments)
                ));
    }

    public Optional<String> getPropertyValue(final ApplicationPropertyKey key, final Object... arguments) {
        final Optional<ApplicationPropertyDo> property = getApplicationProperty(key, arguments);
        return property.map(ApplicationPropertyDo::getValue);
    }

    public Map<String, String> getProperties(final String group) {
        final List<ApplicationPropertyDo> properties = applicationPropertyRepository.findByGroup(group);
        return properties.stream().collect(Collectors.toMap(ApplicationPropertyDo::getKey, ApplicationPropertyDo::getValue));
    }

    private Optional<ApplicationPropertyDo> getApplicationProperty(final ApplicationPropertyKey key, final Object... arguments) {
        return applicationPropertyRepository.findByGroupAndKey(key.getGroup(), key.getKey(arguments));
    }

    public String setApplicationProperty(final String group, final String key, final String value) {
        final ApplicationPropertyDo applicationPropertyDo = applicationPropertyRepository.save(
                ApplicationPropertyDo.builder()
                        .group(group)
                        .key(key)
                        .value(value)
                        .build()
        );
        return applicationPropertyDo.getValue();
    }
}
