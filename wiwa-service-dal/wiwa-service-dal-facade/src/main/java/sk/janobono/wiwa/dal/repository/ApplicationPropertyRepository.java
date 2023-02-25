package sk.janobono.wiwa.dal.repository;

import sk.janobono.wiwa.dal.domain.ApplicationPropertyDo;

import java.util.Optional;

public interface ApplicationPropertyRepository {
    long count();

    boolean exists(String group, String key, String language);

    Optional<ApplicationPropertyDo> getApplicationProperty(String group, String key, String language);

    ApplicationPropertyDo addApplicationProperty(ApplicationPropertyDo applicationPropertyDo);

    ApplicationPropertyDo setApplicationProperty(ApplicationPropertyDo applicationPropertyDo);

    void deleteApplicationProperty(String group, String key, String language);
}
