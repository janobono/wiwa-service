package sk.janobono.wiwa.api.controller;

import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import sk.janobono.wiwa.api.model.codelist.CodeListItemChangeWebDto;
import sk.janobono.wiwa.api.model.codelist.CodeListItemWebDto;
import sk.janobono.wiwa.business.model.codelist.CodeListItemData;
import sk.janobono.wiwa.dal.domain.CodeListDo;
import sk.janobono.wiwa.dal.repository.CodeListRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class CodeListItemControllerTest extends BaseControllerTest {

    @Autowired
    public CodeListRepository codeListRepository;

    @Test
    void fullTest() {
        final String token = signIn(DEFAULT_MANAGER, PASSWORD).token();

        final HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        final List<CodeListDo> codeLists = new ArrayList<>();
        final List<CodeListItemWebDto> codeListItems = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            final CodeListDo codeListDo = codeListRepository.save(CodeListDo.builder()
                    .code("code-" + i)
                    .name("name-" + i)
                    .build()
            );
            codeLists.add(codeListDo);

            for (int j = 0; j < 5; j++) {
                final CodeListItemWebDto root = addCodeListItem(headers,
                        new CodeListItemChangeWebDto(codeListDo.getId(), null, "code-" + i + "-" + j, "value-" + i + "-" + j)
                );
                for (int k = 0; k < 10; k++) {
                    addCodeListItem(headers,
                            new CodeListItemChangeWebDto(codeListDo.getId(), root.id(), "code-" + i + "-" + j + "-" + k, "value-" + i + "-" + j + "-" + k)
                    );
                }
                codeListItems.add(root);
            }
        }

        for (final CodeListItemWebDto codeListItem : codeListItems) {
            assertThat(codeListItem)
                    .usingRecursiveComparison(
                            RecursiveComparisonConfiguration.builder()
                                    .withIgnoredFields("leafNode")
                                    .build()
                    )
                    .isEqualTo(getCodeListItem(headers, codeListItem.id()));
        }

        codeListItems.clear();
        codeLists.forEach(codeList -> {
            codeListItems.addAll(getCodeListItems(headers, codeList.getId(), true, null, null, null, null, null, Pageable.unpaged()).stream().toList());
        });

        for (final CodeListItemWebDto codeListItem : codeListItems) {
            assertThat(codeListItem).usingRecursiveComparison().isEqualTo(getCodeListItem(headers, codeListItem.id()));
        }

        final List<CodeListItemWebDto> searchCodeListItems = getCodeListItems(headers, 1L, true, null, "value-0", null, null, null, Pageable.unpaged()).stream().toList();
        assertThat(searchCodeListItems.size()).isEqualTo(5);

        final List<CodeListItemWebDto> codeCodeListItems = getCodeListItems(headers, 1L, true, null, null, "code-0-0", null, null, Pageable.unpaged()).stream().toList();
        assertThat(codeCodeListItems.size()).isEqualTo(1);

        final List<CodeListItemWebDto> valueCodeListItems = getCodeListItems(headers, 1L, true, null, null, null, "VALUE", null, Pageable.unpaged()).stream().toList();
        assertThat(valueCodeListItems.size()).isEqualTo(5);

        final List<CodeListItemWebDto> treeCodeListItems = getCodeListItems(headers, 1L, null, null, null, null, null, "code-0-4", Pageable.unpaged()).stream().toList();
        assertThat(treeCodeListItems.size()).isEqualTo(11);

        final CodeListItemWebDto changedCodeListItem = setCodeListItem(headers, 1L, new CodeListItemChangeWebDto(1L, null, "code-x", "value-x"));
        assertThat(changedCodeListItem.code()).isEqualTo("code-x");
        assertThat(changedCodeListItem.value()).isEqualTo("value-x");
        assertThat(changedCodeListItem.leafNode()).isFalse();

        List<CodeListItemWebDto> rootCodeListItems = getCodeListItems(headers, 1L, true, null, null, null, null, null, Pageable.unpaged()).stream().toList();
        assertThat(rootCodeListItems.size()).isEqualTo(5);

        final CodeListItemWebDto movedItem = rootCodeListItems.getFirst();
        moveCodeListItemDown(headers, movedItem.id());
        rootCodeListItems = getCodeListItems(headers, 1L, true, null, null, null, null, null, Pageable.unpaged()).stream().toList();
        assertThat(movedItem).usingRecursiveComparison().isEqualTo(rootCodeListItems.get(1));
        moveCodeListItemUp(headers, movedItem.id());
        rootCodeListItems = getCodeListItems(headers, 1L, true, null, null, null, null, null, Pageable.unpaged()).stream().toList();
        assertThat(movedItem).usingRecursiveComparison().isEqualTo(rootCodeListItems.getFirst());

        rootCodeListItems.forEach(rootCategory -> getCodeListItems(headers, rootCategory.codeListId(), null, rootCategory.id(), null, null, null, null, Pageable.unpaged())
                .forEach(child -> {
                    assertThat(child.leafNode()).isTrue();
                }));

        codeListItems.forEach(codeListItemSo -> {
            getCodeListItems(headers, codeListItemSo.codeListId(), null, codeListItemSo.id(), null, null, null, null, Pageable.unpaged())
                    .forEach(child -> deleteCodeListItem(headers, child.id()));
            deleteCodeListItem(headers, codeListItemSo.id());
        });
    }

    private CodeListItemWebDto getCodeListItem(final HttpHeaders headers, final Long id) {
        return getEntity(CodeListItemWebDto.class, headers, "/code-list-items", id);
    }

    private Page<CodeListItemWebDto> getCodeListItems(final HttpHeaders headers, final Long codeListId, final Boolean root, final Long parentId, final String searchField, final String code, final String value, final String treeCode, final Pageable pageable) {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        addToParams(params, "codeListId", codeListId);
        addToParams(params, "root", root);
        addToParams(params, "parentId", parentId);
        addToParams(params, "searchField", searchField);
        addToParams(params, "code", code);
        addToParams(params, "value", value);
        addToParams(params, "treeCode", treeCode);
        return getEntities(CodeListItemWebDto.class, headers, "/code-list-items", params, pageable);
    }

    protected CodeListItemWebDto addCodeListItem(final HttpHeaders headers, final CodeListItemChangeWebDto data) {
        return addEntity(CodeListItemWebDto.class, headers, "/code-list-items", data);
    }

    private CodeListItemWebDto setCodeListItem(final HttpHeaders headers, final Long id, final CodeListItemChangeWebDto data) {
        return setEntity(CodeListItemWebDto.class, headers, "/code-list-items", id, data);
    }

    private void deleteCodeListItem(final HttpHeaders headers, final Long id) {
        deleteEntity(headers, "/code-list-items", id);
    }

    private void moveCodeListItemUp(final HttpHeaders headers, final Long id) {
        final ResponseEntity<CodeListItemData> response = restTemplate.exchange(getURI("/code-list-items/{id}/move-up", Map.of("id", Long.toString(id))), HttpMethod.PATCH, new HttpEntity<>(headers), CodeListItemData.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    private void moveCodeListItemDown(final HttpHeaders headers, final Long id) {
        final ResponseEntity<CodeListItemData> response = restTemplate.exchange(getURI("/code-list-items/{id}/move-down", Map.of("id", Long.toString(id))), HttpMethod.PATCH, new HttpEntity<>(headers), CodeListItemData.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }
}
