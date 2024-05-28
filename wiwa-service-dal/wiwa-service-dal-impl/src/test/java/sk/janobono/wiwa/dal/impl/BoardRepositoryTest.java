package sk.janobono.wiwa.dal.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import sk.janobono.wiwa.dal.domain.BoardDo;
import sk.janobono.wiwa.dal.domain.CodeListDo;
import sk.janobono.wiwa.dal.domain.CodeListItemDo;
import sk.janobono.wiwa.dal.model.BoardSearchCriteriaDo;
import sk.janobono.wiwa.dal.repository.BoardCodeListItemRepository;
import sk.janobono.wiwa.dal.repository.BoardRepository;
import sk.janobono.wiwa.dal.repository.CodeListItemRepository;
import sk.janobono.wiwa.dal.repository.CodeListRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class BoardRepositoryTest extends BaseRepositoryTest {

    @Autowired
    public BoardRepository boardRepository;

    @Autowired
    public CodeListRepository codeListRepository;

    @Autowired
    public CodeListItemRepository codeListItemRepository;

    @Autowired
    public BoardCodeListItemRepository boardCodeListItemRepository;

    @Test
    void fullTest() {
        final CodeListDo codeList = codeListRepository.save(CodeListDo.builder()
                .code("code1")
                .name("name1")
                .build()
        );
        final CodeListItemDo codeListItem = codeListItemRepository.save(CodeListItemDo.builder()
                .codeListId(codeList.getId())
                .treeCode("code")
                .code("code")
                .value("value")
                .sortNum(0)
                .build()
        );

        final List<BoardDo> boards = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            final BoardDo board = boardRepository.save(BoardDo.builder()
                    .code("code%d".formatted(i))
                    .boardCode("boardCode%d".formatted(i))
                    .structureCode("structureCode%d".formatted(i))
                    .name("name%d".formatted(i))
                    .orientation(false)
                    .weight(BigDecimal.valueOf(i).setScale(3, RoundingMode.HALF_UP))
                    .length(BigDecimal.valueOf(i).setScale(3, RoundingMode.HALF_UP))
                    .width(BigDecimal.valueOf(i).setScale(3, RoundingMode.HALF_UP))
                    .thickness(BigDecimal.valueOf(i).setScale(3, RoundingMode.HALF_UP))
                    .price(BigDecimal.valueOf(i).setScale(3, RoundingMode.HALF_UP))
                    .build());
            if (i % 2 == 0) {
                boardCodeListItemRepository.saveAll(board.getId(), List.of(codeListItem.getId()));
            }
            boards.add(board);
        }

        assertThat(boardRepository.countByCode("NOT FOUND")).isEqualTo(0);
        assertThat(boardRepository.countByIdNotAndCode(-1L, "code0")).isEqualTo(1);
        assertThat(boardRepository.existsById(-1L)).isFalse();

        Optional<BoardDo> saved = boardRepository.findById(-1L);
        assertThat(saved.isEmpty()).isTrue();

        for (final BoardDo board : boards) {
            assertThat(boardRepository.countByCode(board.getCode())).isEqualTo(1);
            assertThat(boardRepository.countByIdNotAndCode(board.getId(), board.getCode())).isEqualTo(0);
            assertThat(boardRepository.existsById(board.getId())).isTrue();
            saved = boardRepository.findById(board.getId());
            assertThat(saved.isPresent()).isTrue();
            assertThat(saved.get()).usingRecursiveComparison().isEqualTo(board);
        }

        Page<BoardDo> searchResult = boardRepository.findAll(
                BoardSearchCriteriaDo.builder().build(),
                Pageable.unpaged());
        assertThat(searchResult.getTotalElements()).isEqualTo(10);
        assertThat(searchResult.getTotalPages()).isEqualTo(1);
        assertThat(searchResult.getSize()).isEqualTo(10);
        assertThat(searchResult.getContent().size()).isEqualTo(10);
        assertThat(searchResult.getContent().getFirst()).isEqualTo(boards.getFirst());

        searchResult = boardRepository.findAll(
                BoardSearchCriteriaDo.builder().build(),
                PageRequest.of(0, 10, Sort.Direction.DESC, "anything"));
        assertThat(searchResult.getTotalElements()).isEqualTo(10);
        assertThat(searchResult.getTotalPages()).isEqualTo(1);
        assertThat(searchResult.getSize()).isEqualTo(10);
        assertThat(searchResult.getContent().size()).isEqualTo(10);
        assertThat(searchResult.getContent().getFirst()).isEqualTo(boards.getLast());

        for (final BoardDo board : boards) {
            boardRepository.save(board);
        }

        for (final BoardDo board : boards) {
            boardRepository.deleteById(board.getId());
        }

        searchResult = boardRepository.findAll(
                BoardSearchCriteriaDo.builder().build(),
                Pageable.unpaged());
        assertThat(searchResult.getTotalElements()).isEqualTo(0);
        assertThat(searchResult.getTotalPages()).isEqualTo(1);
        assertThat(searchResult.getSize()).isEqualTo(0);
        assertThat(searchResult.getContent().size()).isEqualTo(0);
    }
}
