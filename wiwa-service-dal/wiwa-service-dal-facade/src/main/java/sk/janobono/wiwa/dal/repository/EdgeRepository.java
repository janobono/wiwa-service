package sk.janobono.wiwa.dal.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sk.janobono.wiwa.dal.domain.EdgeDo;
import sk.janobono.wiwa.dal.model.EdgeSearchCriteriaDo;

import java.util.Optional;

public interface EdgeRepository {

    int countByCode(String code);

    int countByIdNotAndCode(Long id, String code);

    void deleteById(Long id);

    boolean existsById(Long id);

    Page<EdgeDo> findAll(EdgeSearchCriteriaDo criteria, Pageable pageable);

    Optional<EdgeDo> findById(Long id);

    EdgeDo save(EdgeDo edgeDo);
}
