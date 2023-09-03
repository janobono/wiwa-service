package sk.janobono.wiwa.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import sk.janobono.wiwa.dal.domain.ApplicationPropertyDo;
import sk.janobono.wiwa.dal.domain.ApplicationPropertyKeyDo;

public interface ApplicationPropertyRepository extends JpaRepository<ApplicationPropertyDo, ApplicationPropertyKeyDo> {
    @Modifying
    @Query("delete from ApplicationPropertyDo a where a.key=?1")
    void deleteById(ApplicationPropertyKeyDo key);
}
