package sk.janobono.wiwa.dal.repository;

import sk.janobono.wiwa.dal.domain.BoardImageDo;
import sk.janobono.wiwa.dal.model.ApplicationImageInfoDo;

import java.util.List;
import java.util.Optional;

public interface BoardImageRepository {

    void deleteById(final Long id);

    List<ApplicationImageInfoDo> findAllByBoardId(final Long boardId);

    Optional<BoardImageDo> findByBoardIdAndFileName(final Long boardId, final String fileName);

    BoardImageDo save(final BoardImageDo boardImageDo);
}
