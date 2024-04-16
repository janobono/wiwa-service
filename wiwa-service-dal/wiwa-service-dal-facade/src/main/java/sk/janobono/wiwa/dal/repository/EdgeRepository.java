package sk.janobono.wiwa.dal.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sk.janobono.wiwa.dal.domain.EdgeDo;
import sk.janobono.wiwa.dal.model.EdgeSearchCriteriaDo;

import java.util.Optional;

public interface EdgeRepository {

    int countByCode(final String code);

    int countByIdNotAndCode(final Long id, final String code);

    void deleteById(final Long id);

    boolean existsById(final Long id);

    Page<EdgeDo> findAll(final EdgeSearchCriteriaDo criteria, final Pageable pageable);

    Optional<EdgeDo> findById(final Long id);

    EdgeDo save(final EdgeDo edgeDo);
}
