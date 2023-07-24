package sk.janobono.wiwa.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sk.janobono.wiwa.dal.domain.ApplicationImageDo;

public interface ApplicationImageRepository extends JpaRepository<ApplicationImageDo, String> {
}
