package sk.janobono.wiwa.dal.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import sk.janobono.wiwa.dal.domain.CodeListDo;
import sk.janobono.wiwa.dal.model.CodeListSearchCriteriaDo;
import sk.janobono.wiwa.dal.repository.CodeListRepository;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CodeListRepositoryTest extends BaseRepositoryTest {

    @Autowired
    public CodeListRepository codeListRepository;

    @Test
    void fullTest() {
        final List<CodeListDo> codeLists = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            codeLists.add(
                    codeListRepository.save(CodeListDo.builder()
                            .code("code%d".formatted(i))
                            .name("name%d".formatted(i))
                            .build())
            );
        }

        assertThat(codeListRepository.countByCode("NOT FOUND")).isEqualTo(0);
        assertThat(codeListRepository.countByIdNotAndCode(-1L, "code0")).isEqualTo(1);
        assertThat(codeListRepository.existsById(-1L)).isFalse();

        Optional<CodeListDo> saved = codeListRepository.findById(-1L);
        assertThat(saved.isEmpty()).isTrue();

        for (final CodeListDo codeList : codeLists) {
            assertThat(codeListRepository.countByCode(codeList.getCode())).isEqualTo(1);
            assertThat(codeListRepository.countByIdNotAndCode(codeList.getId(), codeList.getCode())).isEqualTo(0);
            assertThat(codeListRepository.existsById(codeList.getId())).isTrue();
            saved = codeListRepository.findById(codeList.getId());
            assertThat(saved.isPresent()).isTrue();
            assertThat(saved.get()).usingRecursiveComparison().isEqualTo(codeList);
        }

        Page<CodeListDo> searchResult = codeListRepository.findAll(CodeListSearchCriteriaDo.builder().build(), Pageable.unpaged());
        assertThat(searchResult.getTotalElements()).isEqualTo(10);
        assertThat(searchResult.getTotalPages()).isEqualTo(1);
        assertThat(searchResult.getSize()).isEqualTo(10);
        assertThat(searchResult.getContent().size()).isEqualTo(10);

        searchResult = codeListRepository.findAll(CodeListSearchCriteriaDo.builder()
                        .searchField("NAME")
                        .build(),
                PageRequest.of(0, 5, Sort.Direction.ASC, "anything"));
        assertThat(searchResult.getTotalElements()).isEqualTo(10);
        assertThat(searchResult.getTotalPages()).isEqualTo(2);
        assertThat(searchResult.getSize()).isEqualTo(5);
        assertThat(searchResult.getContent().size()).isEqualTo(5);
        assertThat(searchResult.getContent().getFirst()).isEqualTo(codeLists.getFirst());

        searchResult = codeListRepository.findAll(CodeListSearchCriteriaDo.builder()
                        .searchField("NAME")
                        .build(),
                PageRequest.of(0, 5, Sort.Direction.DESC, "anything"));
        assertThat(searchResult.getTotalElements()).isEqualTo(10);
        assertThat(searchResult.getTotalPages()).isEqualTo(2);
        assertThat(searchResult.getSize()).isEqualTo(5);
        assertThat(searchResult.getContent().size()).isEqualTo(5);
        assertThat(searchResult.getContent().getFirst()).isEqualTo(codeLists.getLast());

        searchResult = codeListRepository.findAll(CodeListSearchCriteriaDo.builder()
                        .code("code0")
                        .build(),
                PageRequest.of(0, 5, Sort.Direction.DESC, "anything"));
        assertThat(searchResult.getTotalElements()).isEqualTo(1);
        assertThat(searchResult.getTotalPages()).isEqualTo(1);
        assertThat(searchResult.getSize()).isEqualTo(5);
        assertThat(searchResult.getContent().size()).isEqualTo(1);

        searchResult = codeListRepository.findAll(CodeListSearchCriteriaDo.builder()
                        .name("name0")
                        .build(),
                PageRequest.of(0, 5, Sort.Direction.DESC, "anything"));
        assertThat(searchResult.getTotalElements()).isEqualTo(1);
        assertThat(searchResult.getTotalPages()).isEqualTo(1);
        assertThat(searchResult.getSize()).isEqualTo(5);
        assertThat(searchResult.getContent().size()).isEqualTo(1);

        for (final CodeListDo codeList : codeLists) {
            codeListRepository.save(CodeListDo.builder()
                    .id(codeList.getId())
                    .code(codeList.getCode())
                    .name("changeMe%s".formatted(codeList.getName()))
                    .build());
        }

        for (final CodeListDo codeList : codeLists) {
            codeListRepository.deleteById(codeList.getId());
        }

        searchResult = codeListRepository.findAll(CodeListSearchCriteriaDo.builder().build(), Pageable.unpaged());
        assertThat(searchResult.getTotalElements()).isEqualTo(0);
        assertThat(searchResult.getTotalPages()).isEqualTo(1);
        assertThat(searchResult.getSize()).isEqualTo(0);
        assertThat(searchResult.getContent().size()).isEqualTo(0);
    }
}
