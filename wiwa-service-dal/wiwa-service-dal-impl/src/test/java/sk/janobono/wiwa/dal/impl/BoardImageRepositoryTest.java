package sk.janobono.wiwa.dal.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sk.janobono.wiwa.dal.domain.BoardDo;
import sk.janobono.wiwa.dal.domain.BoardImageDo;
import sk.janobono.wiwa.dal.domain.EdgeDo;
import sk.janobono.wiwa.dal.repository.BoardImageRepository;
import sk.janobono.wiwa.dal.repository.BoardRepository;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class BoardImageRepositoryTest extends BaseRepositoryTest {

    @Autowired
    public BoardImageRepository boardImageRepository;

    @Autowired
    public BoardRepository boardRepository;

    @Test
    void fullTest() {
        final BoardDo board = boardRepository.save(BoardDo.builder()
                .code("code")
                .boardCode("boardCode")
                .structureCode("structureCode")
                .name("name")
                .orientation(false)
                .weight(BigDecimal.ZERO)
                .length(BigDecimal.ZERO)
                .width(BigDecimal.ZERO)
                .thickness(BigDecimal.ZERO)
                .price(BigDecimal.ZERO)
                .build());

        BoardImageDo boardImage = boardImageRepository.save(BoardImageDo.builder()
                .boardId(board.getId())
                .fileType("fileType")
                .data("data".getBytes(StandardCharsets.UTF_8))
                .build());

        boardImage = boardImageRepository.save(BoardImageDo.builder()
                .boardId(boardImage.getBoardId())
                .fileType("fileType")
                .data("data".getBytes(StandardCharsets.UTF_8))
                .build());

        Optional<BoardImageDo> saved = boardImageRepository.findByBoardId(board.getId());
        assertThat(saved.isPresent()).isTrue();
        assertThat(saved.get()).usingRecursiveComparison().isEqualTo(boardImage);

        boardImageRepository.deleteByBoardId(board.getId());

        saved = boardImageRepository.findByBoardId(board.getId());
        assertThat(saved.isEmpty()).isTrue();
    }
}
