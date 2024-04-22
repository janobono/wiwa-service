package sk.janobono.wiwa.dal.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sk.janobono.wiwa.dal.domain.BoardDo;
import sk.janobono.wiwa.dal.model.BoardSearchCriteriaDo;

import java.util.Optional;

public interface BoardRepository {

    int countByCode(String code);

    int countByIdNotAndCode(long id, String code);

    void deleteById(long id);

    boolean existsById(long id);

    Page<BoardDo> findAll(BoardSearchCriteriaDo criteria, Pageable pageable);

    Optional<BoardDo> findById(long id);

    BoardDo save(BoardDo boardDo);
}
