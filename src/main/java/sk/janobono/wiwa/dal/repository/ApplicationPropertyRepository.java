package sk.janobono.wiwa.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sk.janobono.wiwa.dal.domain.ApplicationPropertyDo;
import sk.janobono.wiwa.dal.domain.ApplicationPropertyKeyDo;

public interface ApplicationPropertyRepository extends JpaRepository<ApplicationPropertyDo, ApplicationPropertyKeyDo> {
}
