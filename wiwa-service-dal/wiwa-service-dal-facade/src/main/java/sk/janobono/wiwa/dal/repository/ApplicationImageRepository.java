package sk.janobono.wiwa.dal.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sk.janobono.wiwa.dal.domain.ApplicationImageDo;
import sk.janobono.wiwa.dal.model.ApplicationImageInfoDo;

import java.util.Optional;

public interface ApplicationImageRepository {

    boolean exists(String fileName);

    Page<ApplicationImageInfoDo> getApplicationImages(Pageable pageable);

    Optional<ApplicationImageDo> getApplicationImage(String fileName);

    ApplicationImageInfoDo addApplicationImage(ApplicationImageDo applicationImageDo);

    ApplicationImageInfoDo setApplicationImage(ApplicationImageDo applicationImageDo);

    void deleteApplicationImage(String fileName);
}
