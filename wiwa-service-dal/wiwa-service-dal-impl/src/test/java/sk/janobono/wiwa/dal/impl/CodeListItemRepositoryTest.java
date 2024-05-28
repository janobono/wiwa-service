package sk.janobono.wiwa.dal.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import sk.janobono.wiwa.dal.domain.CodeListDo;
import sk.janobono.wiwa.dal.domain.CodeListItemDo;
import sk.janobono.wiwa.dal.model.CodeListItemSearchCriteriaDo;
import sk.janobono.wiwa.dal.repository.CodeListItemRepository;
import sk.janobono.wiwa.dal.repository.CodeListRepository;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CodeListItemRepositoryTest extends BaseRepositoryTest {

    @Autowired
    public CodeListItemRepository codeListItemRepository;

    @Autowired
    public CodeListRepository codeListRepository;

    @Test
    void fullTest() {
        final CodeListDo codeList1 = codeListRepository.save(CodeListDo.builder()
                .code("code1")
                .name("name1")
                .build()
        );
        final CodeListDo codeList2 = codeListRepository.save(CodeListDo.builder()
                .code("code2")
                .name("name2")
                .build()
        );

        final List<CodeListItemDo> codeListItems1 = new LinkedList<>();
        final List<CodeListItemDo> codeListItems2 = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            codeListItems1.add(codeListItemRepository.save(CodeListItemDo.builder()
                    .codeListId(codeList1.getId())
                    .treeCode("codeA%d".formatted(i))
                    .code("codeA%d".formatted(i))
                    .value("valueA%d".formatted(i))
                    .sortNum(i)
                    .build())
            );
            codeListItems2.add(codeListItemRepository.save(CodeListItemDo.builder()
                    .codeListId(codeList2.getId())
                    .treeCode("codeB%d".formatted(i))
                    .code("codeB%d".formatted(i))
                    .value("valueB%d".formatted(i))
                    .sortNum(i)
                    .build())
            );
        }

        assertThat(codeListItemRepository.countByCodeListIdAndParentIdNull(codeList1.getId())).isEqualTo(10);
        assertThat(codeListItemRepository.countByCodeListIdAndParentIdNull(codeList2.getId())).isEqualTo(10);
        assertThat(codeListItemRepository.countByCode("NOT FOUND")).isEqualTo(0);

        Optional<CodeListItemDo> saved = codeListItemRepository.findById(-1L);
        assertThat(saved.isPresent()).isFalse();

        for (final CodeListItemDo item : codeListItems1) {
            assertThat(codeListItemRepository.countByCode(item.getCode())).isEqualTo(1);
            assertThat(codeListItemRepository.countByIdNotAndCode(item.getId(), item.getCode())).isEqualTo(0);
            assertThat(codeListItemRepository.countByParentId(item.getId())).isEqualTo(0);
            assertThat(codeListItemRepository.existsById(item.getId())).isTrue();
            saved = codeListItemRepository.findById(item.getId());
            assertThat(saved.isPresent()).isTrue();
            assertThat(saved.get()).usingRecursiveComparison().isEqualTo(item);
        }

        for (final CodeListItemDo item : codeListItems2) {
            assertThat(codeListItemRepository.countByCode(item.getCode())).isEqualTo(1);
            assertThat(codeListItemRepository.countByIdNotAndCode(item.getId(), item.getCode())).isEqualTo(0);
            assertThat(codeListItemRepository.countByParentId(item.getId())).isEqualTo(0);
            assertThat(codeListItemRepository.existsById(item.getId())).isTrue();
            saved = codeListItemRepository.findById(item.getId());
            assertThat(saved.isPresent()).isTrue();
            assertThat(saved.get()).usingRecursiveComparison().isEqualTo(item);
        }

        Page<CodeListItemDo> searchResult = codeListItemRepository.findAll(
                CodeListItemSearchCriteriaDo.builder().build(),
                Pageable.unpaged()
        );
        assertThat(searchResult.getTotalElements()).isEqualTo(20);
        assertThat(searchResult.getTotalPages()).isEqualTo(1);
        assertThat(searchResult.getSize()).isEqualTo(20);
        assertThat(searchResult.getContent().size()).isEqualTo(20);

        searchResult = codeListItemRepository.findAll(
                CodeListItemSearchCriteriaDo.builder()
                        .codeListId(codeList1.getId())
                        .root(true)
                        .build(),
                PageRequest.of(0, 5, Sort.Direction.ASC, "anything")
        );
        assertThat(searchResult.getTotalElements()).isEqualTo(10);
        assertThat(searchResult.getTotalPages()).isEqualTo(2);
        assertThat(searchResult.getSize()).isEqualTo(5);
        assertThat(searchResult.getContent().size()).isEqualTo(5);
        assertThat(searchResult.getContent().getFirst()).isEqualTo(codeListItems1.getFirst());

        searchResult = codeListItemRepository.findAll(
                CodeListItemSearchCriteriaDo.builder()
                        .codeListId(codeList1.getId())
                        .parentId(-1L)
                        .build(),
                PageRequest.of(0, 5, Sort.Direction.ASC, "anything")
        );
        assertThat(searchResult.getTotalElements()).isEqualTo(0);
        assertThat(searchResult.getTotalPages()).isEqualTo(0);
        assertThat(searchResult.getSize()).isEqualTo(5);
        assertThat(searchResult.getContent().size()).isEqualTo(0);

        searchResult = codeListItemRepository.findAll(
                CodeListItemSearchCriteriaDo.builder()
                        .codeListId(codeList1.getId())
                        .build(),
                PageRequest.of(0, 5, Sort.Direction.DESC, "anything")
        );
        assertThat(searchResult.getTotalElements()).isEqualTo(10);
        assertThat(searchResult.getTotalPages()).isEqualTo(2);
        assertThat(searchResult.getSize()).isEqualTo(5);
        assertThat(searchResult.getContent().size()).isEqualTo(5);
        assertThat(searchResult.getContent().getFirst()).isEqualTo(codeListItems1.getLast());

        int i = 0;
        for (final CodeListItemDo item : codeListItems1) {
            codeListItemRepository.save(CodeListItemDo.builder()
                    .id(item.getId())
                    .codeListId(codeList1.getId())
                    .treeCode("codeX%d".formatted(i))
                    .code("codeX%d".formatted(i))
                    .value("valueX%d".formatted(i))
                    .sortNum(item.getSortNum())
                    .build());
            i++;
        }

        i = 0;
        for (final CodeListItemDo item : codeListItems2) {
            codeListItemRepository.save(CodeListItemDo.builder()
                    .id(item.getId())
                    .codeListId(codeList1.getId())
                    .treeCode("codeY%d".formatted(i))
                    .code("codeY%d".formatted(i))
                    .value("valueY%d".formatted(i))
                    .sortNum(item.getSortNum())
                    .build());
            i++;
        }

        for (final CodeListItemDo item : codeListItems1) {
            codeListItemRepository.deleteById(item.getId());
        }

        for (final CodeListItemDo item : codeListItems2) {
            codeListItemRepository.deleteById(item.getId());
        }

        searchResult = codeListItemRepository.findAll(
                CodeListItemSearchCriteriaDo.builder().build(),
                Pageable.unpaged()
        );
        assertThat(searchResult.getTotalElements()).isEqualTo(0);
        assertThat(searchResult.getTotalPages()).isEqualTo(1);
        assertThat(searchResult.getSize()).isEqualTo(0);
        assertThat(searchResult.getContent().size()).isEqualTo(0);
    }
}
