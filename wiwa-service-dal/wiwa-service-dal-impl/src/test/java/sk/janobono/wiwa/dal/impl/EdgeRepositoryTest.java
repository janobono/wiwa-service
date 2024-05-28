package sk.janobono.wiwa.dal.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import sk.janobono.wiwa.dal.domain.CodeListDo;
import sk.janobono.wiwa.dal.domain.CodeListItemDo;
import sk.janobono.wiwa.dal.domain.EdgeDo;
import sk.janobono.wiwa.dal.model.EdgeSearchCriteriaDo;
import sk.janobono.wiwa.dal.repository.CodeListItemRepository;
import sk.janobono.wiwa.dal.repository.CodeListRepository;
import sk.janobono.wiwa.dal.repository.EdgeCodeListItemRepository;
import sk.janobono.wiwa.dal.repository.EdgeRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class EdgeRepositoryTest extends BaseRepositoryTest {

    @Autowired
    public EdgeRepository edgeRepository;

    @Autowired
    public CodeListRepository codeListRepository;

    @Autowired
    public CodeListItemRepository codeListItemRepository;

    @Autowired
    public EdgeCodeListItemRepository edgeCodeListItemRepository;

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

        final List<EdgeDo> edges = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            final EdgeDo edge = edgeRepository.save(EdgeDo.builder()
                    .code("code%d".formatted(i))
                    .name("name%d".formatted(i))
                    .description("description%d".formatted(i))
                    .weight(BigDecimal.valueOf(i).setScale(3, RoundingMode.HALF_UP))
                    .width(BigDecimal.valueOf(i).setScale(3, RoundingMode.HALF_UP))
                    .thickness(BigDecimal.valueOf(i).setScale(3, RoundingMode.HALF_UP))
                    .price(BigDecimal.valueOf(i).setScale(3, RoundingMode.HALF_UP))
                    .build());
            if (i % 2 == 0) {
                edgeCodeListItemRepository.saveAll(edge.getId(), List.of(codeListItem.getId()));
            }
            edges.add(edge);
        }

        assertThat(edgeRepository.countByCode("NOT FOUND")).isEqualTo(0);
        assertThat(edgeRepository.countByIdNotAndCode(-1L, "code0")).isEqualTo(1);
        assertThat(edgeRepository.existsById(-1L)).isFalse();

        Optional<EdgeDo> saved = edgeRepository.findById(-1L);
        assertThat(saved.isEmpty()).isTrue();

        for (final EdgeDo edge : edges) {
            assertThat(edgeRepository.countByCode(edge.getCode())).isEqualTo(1);
            assertThat(edgeRepository.countByIdNotAndCode(edge.getId(), edge.getCode())).isEqualTo(0);
            assertThat(edgeRepository.existsById(edge.getId())).isTrue();
            saved = edgeRepository.findById(edge.getId());
            assertThat(saved.isPresent()).isTrue();
            assertThat(saved.get()).usingRecursiveComparison().isEqualTo(edge);
        }

        Page<EdgeDo> searchResult = edgeRepository.findAll(
                EdgeSearchCriteriaDo.builder().build(),
                Pageable.unpaged());
        assertThat(searchResult.getTotalElements()).isEqualTo(10);
        assertThat(searchResult.getTotalPages()).isEqualTo(1);
        assertThat(searchResult.getSize()).isEqualTo(10);
        assertThat(searchResult.getContent().size()).isEqualTo(10);
        assertThat(searchResult.getContent().getFirst()).isEqualTo(edges.getFirst());

        searchResult = edgeRepository.findAll(
                EdgeSearchCriteriaDo.builder().build(),
                PageRequest.of(0, 10, Sort.Direction.DESC, "anything"));
        assertThat(searchResult.getTotalElements()).isEqualTo(10);
        assertThat(searchResult.getTotalPages()).isEqualTo(1);
        assertThat(searchResult.getSize()).isEqualTo(10);
        assertThat(searchResult.getContent().size()).isEqualTo(10);
        assertThat(searchResult.getContent().getFirst()).isEqualTo(edges.getLast());

        for (final EdgeDo edge : edges) {
            edgeRepository.save(edge);
        }

        for (final EdgeDo edge : edges) {
            edgeRepository.deleteById(edge.getId());
        }

        searchResult = edgeRepository.findAll(
                EdgeSearchCriteriaDo.builder().build(),
                Pageable.unpaged());
        assertThat(searchResult.getTotalElements()).isEqualTo(0);
        assertThat(searchResult.getTotalPages()).isEqualTo(1);
        assertThat(searchResult.getSize()).isEqualTo(0);
        assertThat(searchResult.getContent().size()).isEqualTo(0);
    }
}
