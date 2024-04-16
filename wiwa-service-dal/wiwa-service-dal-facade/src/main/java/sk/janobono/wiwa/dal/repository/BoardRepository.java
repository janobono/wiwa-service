package sk.janobono.wiwa.dal.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sk.janobono.wiwa.dal.domain.BoardDo;
import sk.janobono.wiwa.dal.model.BoardSearchCriteriaDo;

import java.util.Optional;

public interface BoardRepository {

    int countByCode(final String code);

    int countByIdNotAndCode(final Long id, final String code);

    void deleteById(final Long id);

    boolean existsById(final Long id);

    Page<BoardDo> findAll(final BoardSearchCriteriaDo criteria, final Pageable pageable);

    Optional<BoardDo> findById(final Long id);

    BoardDo save(final BoardDo boardDo);
}
