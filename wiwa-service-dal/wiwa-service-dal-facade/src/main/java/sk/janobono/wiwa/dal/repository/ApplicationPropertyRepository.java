package sk.janobono.wiwa.dal.repository;

import sk.janobono.wiwa.dal.domain.ApplicationPropertyDo;

import java.util.Optional;

public interface ApplicationPropertyRepository {

    void deleteByKey(final String key);

    Optional<ApplicationPropertyDo> findByKey(final String key);

    ApplicationPropertyDo save(final ApplicationPropertyDo applicationPropertyDo);
}
