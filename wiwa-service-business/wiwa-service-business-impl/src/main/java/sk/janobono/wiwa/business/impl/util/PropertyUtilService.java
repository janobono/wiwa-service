package sk.janobono.wiwa.business.impl.util;

import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.internal.Function;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.dal.domain.ApplicationPropertyDo;
import sk.janobono.wiwa.dal.repository.ApplicationPropertyRepository;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PropertyUtilService {

    private final ApplicationPropertyRepository applicationPropertyRepository;

    public Optional<String> getProperty(final String key) {
        return getProperty(v -> v, key);
    }

    public <T> Optional<T> getProperty(final Function<String, T> mapper, final String key) {
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
