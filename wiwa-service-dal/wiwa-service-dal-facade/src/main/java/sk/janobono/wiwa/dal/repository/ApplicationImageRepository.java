package sk.janobono.wiwa.dal.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sk.janobono.wiwa.dal.domain.ApplicationImageDo;
import sk.janobono.wiwa.dal.model.ApplicationImageInfoDo;

import java.util.Optional;

public interface ApplicationImageRepository {

    void deleteById(String id);

    Page<ApplicationImageInfoDo> findAll(Pageable pageable);

    Optional<ApplicationImageDo> findById(String id);

    ApplicationImageDo save(ApplicationImageDo applicationImageDo);
}
