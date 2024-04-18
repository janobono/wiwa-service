package sk.janobono.wiwa.dal.repository;

import sk.janobono.wiwa.dal.domain.BoardImageDo;
import sk.janobono.wiwa.dal.model.ApplicationImageInfoDo;

import java.util.List;
import java.util.Optional;

public interface BoardImageRepository {

    void deleteById(Long id);

    List<ApplicationImageInfoDo> findAllByBoardId(Long boardId);

    Optional<BoardImageDo> findByBoardIdAndFileName(Long boardId, String fileName);

    BoardImageDo save(BoardImageDo boardImageDo);
}
