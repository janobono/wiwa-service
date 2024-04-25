package sk.janobono.wiwa.dal.repository;

import sk.janobono.wiwa.dal.domain.BoardImageDo;

import java.util.Optional;

public interface BoardImageRepository {

    void deleteByBoardId(long boardId);

    Optional<BoardImageDo> findByBoardId(long boardId);

    BoardImageDo save(BoardImageDo boardImageDo);
}
