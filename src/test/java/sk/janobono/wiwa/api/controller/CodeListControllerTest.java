package sk.janobono.wiwa.api.controller;

import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import sk.janobono.wiwa.business.model.codelist.CodeListDataSo;
import sk.janobono.wiwa.business.model.codelist.CodeListItemDataSo;
import sk.janobono.wiwa.business.model.codelist.CodeListItemSo;
import sk.janobono.wiwa.business.model.codelist.CodeListSo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CodeListControllerTest extends BaseControllerTest {

    @Test
    void fullTest() {
        final String token = signIn(DEFAULT_MANAGER, PASSWORD).token();

        final HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        final List<CodeListSo> codeLists = new ArrayList<>();
        final List<CodeListItemSo> codeListItems = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            final CodeListSo codeList = addCodeList(headers, new CodeListDataSo("code-" + i, "name-" + i));
            codeLists.add(codeList);
            for (int j = 0; j < 5; j++) {
                final CodeListItemSo root = addCodeListItem(headers,
                        new CodeListItemDataSo(codeList.id(), null, "code-" + i + "-" + j, "value-" + i + "-" + j)
                );
                for (int k = 0; k < 10; k++) {
                    addCodeListItem(headers,
                            new CodeListItemDataSo(codeList.id(), root.id(), "code-" + i + "-" + j + "-" + k, "value-" + i + "-" + j + "-" + k)
                    );
                }
                codeListItems.add(root);
            }
        }

        for (final CodeListItemSo codeListItemSo : codeListItems) {
            assertThat(codeListItemSo)
                    .usingRecursiveComparison(
                            RecursiveComparisonConfiguration.builder()
                                    .withIgnoredFields("leafNode")
                                    .build()
                    )
                    .isEqualTo(getCodeListItem(headers, codeListItemSo.id()));
        }

        codeLists.clear();
        codeLists.addAll(getCodeLists(headers, null, null, null, Pageable.unpaged()).stream().toList());

        codeListItems.clear();
        codeLists.forEach(codeListSo -> {
            codeListItems.addAll(getCodeListItems(headers, codeListSo.id(), true, null, null, null, null, null, Pageable.unpaged()).stream().toList());
        });

        for (final CodeListSo codeListSo : codeLists) {
            assertThat(codeListSo).usingRecursiveComparison().isEqualTo(getCodeList(headers, codeListSo.id()));
        }

        for (final CodeListItemSo codeListItemSo : codeListItems) {
            assertThat(codeListItemSo).usingRecursiveComparison().isEqualTo(getCodeListItem(headers, codeListItemSo.id()));
        }

        final List<CodeListSo> searchCodeLists = getCodeLists(headers, "name-", null, null, Pageable.unpaged()).stream().toList();
        assertThat(searchCodeLists.size()).isEqualTo(10);

        final List<CodeListSo> codeCodeLists = getCodeLists(headers, null, "code-1", null, Pageable.unpaged()).stream().toList();
        assertThat(codeCodeLists.size()).isEqualTo(1);

        final List<CodeListSo> nameCodeLists = getCodeLists(headers, null, null, "NAME", Pageable.unpaged()).stream().toList();
        assertThat(nameCodeLists.size()).isEqualTo(10);

        final List<CodeListItemSo> searchCodeListItems = getCodeListItems(headers, 1L, true, null, "value-0", null, null, null, Pageable.unpaged()).stream().toList();
        assertThat(searchCodeListItems.size()).isEqualTo(5);

        final List<CodeListItemSo> codeCodeListItems = getCodeListItems(headers, 1L, true, null, null, "code-0-0", null, null, Pageable.unpaged()).stream().toList();
        assertThat(codeCodeListItems.size()).isEqualTo(1);

        final List<CodeListItemSo> valueCodeListItems = getCodeListItems(headers, 1L, true, null, null, null, "VALUE", null, Pageable.unpaged()).stream().toList();
        assertThat(valueCodeListItems.size()).isEqualTo(5);

        final List<CodeListItemSo> treeCodeListItems = getCodeListItems(headers, 1L, null, null, null, null, null, "code-0-4", Pageable.unpaged()).stream().toList();
        assertThat(treeCodeListItems.size()).isEqualTo(11);

        final CodeListSo changedCodeList = setCodeList(headers, 1L, new CodeListDataSo("code-x", "name-x"));
        assertThat(changedCodeList.code()).isEqualTo("code-x");
        assertThat(changedCodeList.name()).isEqualTo("name-x");

        final CodeListItemSo changedCodeListItem = setCodeListItem(headers, 1L, new CodeListItemDataSo(1L, null, "code-x", "value-x"));
        assertThat(changedCodeListItem.code()).isEqualTo("code-x");
        assertThat(changedCodeListItem.value()).isEqualTo("value-x");
        assertThat(changedCodeListItem.leafNode()).isFalse();

        List<CodeListItemSo> rootCodeListItems = getCodeListItems(headers, 1L, true, null, null, null, null, null, Pageable.unpaged()).stream().toList();
        assertThat(rootCodeListItems.size()).isEqualTo(5);

        final CodeListItemSo movedItem = rootCodeListItems.get(0);
        moveCodeListItemDown(headers, movedItem.id());
        rootCodeListItems = getCodeListItems(headers, 1L, true, null, null, null, null, null, Pageable.unpaged()).stream().toList();
        assertThat(movedItem).usingRecursiveComparison().isEqualTo(rootCodeListItems.get(1));
        moveCodeListItemUp(headers, movedItem.id());
        rootCodeListItems = getCodeListItems(headers, 1L, true, null, null, null, null, null, Pageable.unpaged()).stream().toList();
        assertThat(movedItem).usingRecursiveComparison().isEqualTo(rootCodeListItems.get(0));

        rootCodeListItems.forEach(rootCategory -> {
            getCodeListItems(headers, rootCategory.codeListId(), null, rootCategory.id(), null, null, null, null, Pageable.unpaged())
                    .forEach(child -> {
                        assertThat(child.leafNode()).isTrue();
                    });
        });

        codeListItems.forEach(codeListItemSo -> {
            getCodeListItems(headers, codeListItemSo.codeListId(), null, codeListItemSo.id(), null, null, null, null, Pageable.unpaged())
                    .forEach(child -> {
                        deleteCodeListItem(headers, child.id());
                    });
            deleteCodeListItem(headers, codeListItemSo.id());
        });

        codeLists.forEach(codeListSo -> {
            deleteCodeList(headers, codeListSo.id());
        });
    }

    private CodeListSo getCodeList(final HttpHeaders headers, final Long id) {
        return getEntity(CodeListSo.class, headers, "/code-lists", id);
    }

    private CodeListItemSo getCodeListItem(final HttpHeaders headers, final Long id) {
        return getEntity(CodeListItemSo.class, headers, "/code-lists/items", id);
    }

    private Page<CodeListSo> getCodeLists(final HttpHeaders headers, final String searchField, final String code, final String name, final Pageable pageable) {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        Optional.ofNullable(searchField).ifPresent(v -> addToParams(params, "searchField", v));
        Optional.ofNullable(code).ifPresent(v -> addToParams(params, "code", v));
        Optional.ofNullable(name).ifPresent(v -> addToParams(params, "name", v));
        return getEntities(CodeListSo.class, headers, "/code-lists", params, pageable);
    }

    private Page<CodeListItemSo> getCodeListItems(final HttpHeaders headers, final Long codeListId, final Boolean root, final Long parentId, final String searchField, final String code, final String value, final String treeCode, final Pageable pageable) {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        Optional.ofNullable(codeListId).ifPresent(v -> addToParams(params, "codeListId", v.toString()));
        Optional.ofNullable(root).ifPresent(v -> addToParams(params, "root", v.toString()));
        Optional.ofNullable(parentId).ifPresent(v -> addToParams(params, "parentId", v.toString()));
        Optional.ofNullable(searchField).ifPresent(v -> addToParams(params, "searchField", v));
        Optional.ofNullable(code).ifPresent(v -> addToParams(params, "code", v));
        Optional.ofNullable(value).ifPresent(v -> addToParams(params, "value", v));
        Optional.ofNullable(treeCode).ifPresent(v -> addToParams(params, "treeCode", v));
        return getEntities(CodeListItemSo.class, headers, "/code-lists/items", params, pageable);
    }

    private CodeListSo setCodeList(final HttpHeaders headers, final Long id, final CodeListDataSo data) {
        return setEntity(CodeListSo.class, headers, "/code-lists", id, data);
    }

    private CodeListItemSo setCodeListItem(final HttpHeaders headers, final Long id, final CodeListItemDataSo data) {
        return setEntity(CodeListItemSo.class, headers, "/code-lists/items", id, data);
    }

    private void deleteCodeList(final HttpHeaders headers, final Long id) {
        deleteEntity(headers, "/code-lists", id);
    }

    private void deleteCodeListItem(final HttpHeaders headers, final Long id) {
        deleteEntity(headers, "/code-lists/items", id);
    }

    private void moveCodeListItemUp(final HttpHeaders headers, final Long id) {
        final ResponseEntity<CodeListItemSo> response = restTemplate.exchange(getURI("/code-lists/items/{itemId}/move-up", Map.of("itemId", Long.toString(id))), HttpMethod.PATCH, new HttpEntity<>(headers), CodeListItemSo.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    private void moveCodeListItemDown(final HttpHeaders headers, final Long id) {
        final ResponseEntity<CodeListItemSo> response = restTemplate.exchange(getURI("/code-lists/items/{itemId}/move-down", Map.of("itemId", Long.toString(id))), HttpMethod.PATCH, new HttpEntity<>(headers), CodeListItemSo.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }
}
