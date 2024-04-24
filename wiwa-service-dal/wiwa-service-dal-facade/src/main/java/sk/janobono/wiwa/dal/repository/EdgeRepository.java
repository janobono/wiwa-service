package sk.janobono.wiwa.dal.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sk.janobono.wiwa.dal.domain.EdgeDo;
import sk.janobono.wiwa.dal.model.EdgeSearchCriteriaDo;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EdgeRepository {

    int countByCode(String code);

    int countByIdNotAndCode(long id, String code);

    void deleteById(long id);

    boolean existsById(long id);

    Page<EdgeDo> findAll(EdgeSearchCriteriaDo criteria, Pageable pageable);

    Optional<EdgeDo> findById(long id);

    List<EdgeDo> findAllByIds(Set<Long> ids);

    EdgeDo save(EdgeDo edgeDo);
}
