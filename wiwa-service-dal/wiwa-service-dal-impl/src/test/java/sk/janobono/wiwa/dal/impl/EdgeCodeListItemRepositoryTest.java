package sk.janobono.wiwa.dal.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sk.janobono.wiwa.dal.domain.CodeListDo;
import sk.janobono.wiwa.dal.domain.CodeListItemDo;
import sk.janobono.wiwa.dal.domain.EdgeDo;
import sk.janobono.wiwa.dal.repository.CodeListItemRepository;
import sk.janobono.wiwa.dal.repository.CodeListRepository;
import sk.janobono.wiwa.dal.repository.EdgeCodeListItemRepository;
import sk.janobono.wiwa.dal.repository.EdgeRepository;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EdgeCodeListItemRepositoryTest extends BaseRepositoryTest {

    @Autowired
    public EdgeCodeListItemRepository edgeCodeListItemRepository;

    @Autowired
    public EdgeRepository edgeRepository;

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
        final EdgeDo edge = edgeRepository.save(EdgeDo.builder()
                .code("code")
                .name("name")
                .weight(BigDecimal.ZERO)
                .width(BigDecimal.ZERO)
                .thickness(BigDecimal.ZERO)
                .price(BigDecimal.ZERO)
                .build());

        edgeCodeListItemRepository.saveAll(edge.getId(), List.of(codeListItem.getId()));

        final List<CodeListItemDo> edgeCodeListItems = edgeCodeListItemRepository.findByEdgeId(edge.getId());
        assertThat(edgeCodeListItems).hasSize(1);
        assertThat(edgeCodeListItems.getFirst()).usingRecursiveComparison().isEqualTo(codeListItem);
    }
}
