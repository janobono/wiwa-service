package sk.janobono.wiwa.dal.repository;

import sk.janobono.wiwa.dal.domain.ApplicationPropertyDo;

import java.util.Optional;

public interface ApplicationPropertyRepository {

    void deleteByKey(String key);

    Optional<ApplicationPropertyDo> findByKey(String key);

    ApplicationPropertyDo save(ApplicationPropertyDo applicationPropertyDo);
}
