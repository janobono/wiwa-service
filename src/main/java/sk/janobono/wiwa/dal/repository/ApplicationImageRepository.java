package sk.janobono.wiwa.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import sk.janobono.wiwa.dal.domain.ApplicationImageDo;

public interface ApplicationImageRepository extends JpaRepository<ApplicationImageDo, String> {
    @Modifying
    @Query("delete from ApplicationImageDo a where a.fileName=?1")
    void deleteById(String fileName);
}
