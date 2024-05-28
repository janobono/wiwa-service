package sk.janobono.wiwa.dal.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sk.janobono.wiwa.dal.domain.BoardDo;
import sk.janobono.wiwa.dal.domain.CodeListDo;
import sk.janobono.wiwa.dal.domain.CodeListItemDo;
import sk.janobono.wiwa.dal.repository.BoardCodeListItemRepository;
import sk.janobono.wiwa.dal.repository.BoardRepository;
import sk.janobono.wiwa.dal.repository.CodeListItemRepository;
import sk.janobono.wiwa.dal.repository.CodeListRepository;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BoardCodeListItemRepositoryTest extends BaseRepositoryTest {

    @Autowired
    public BoardCodeListItemRepository boardCodeListItemRepository;

    @Autowired
    public BoardRepository boardRepository;

    @Autowired
    public CodeListRepository codeListRepository;

    @Autowired
    public CodeListItemRepository codeListItemRepository;

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

        boardCodeListItemRepository.saveAll(board.getId(), List.of(codeListItem.getId()));

        final List<CodeListItemDo> edgeCodeListItems = boardCodeListItemRepository.findByBoardId(board.getId());
        assertThat(edgeCodeListItems).hasSize(1);
        assertThat(edgeCodeListItems.getFirst()).usingRecursiveComparison().isEqualTo(codeListItem);
    }
}
