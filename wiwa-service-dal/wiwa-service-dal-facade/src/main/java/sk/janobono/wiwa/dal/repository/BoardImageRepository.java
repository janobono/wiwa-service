package sk.janobono.wiwa.dal.repository;

import sk.janobono.wiwa.dal.domain.BoardImageDo;
import sk.janobono.wiwa.dal.model.ApplicationImageInfoDo;

import java.util.List;
import java.util.Optional;

public interface BoardImageRepository {

    void deleteById(long id);

    List<ApplicationImageInfoDo> findAllByBoardId(long boardId);

    Optional<BoardImageDo> findByBoardIdAndFileName(long boardId, String fileName);

    BoardImageDo save(BoardImageDo boardImageDo);
}
