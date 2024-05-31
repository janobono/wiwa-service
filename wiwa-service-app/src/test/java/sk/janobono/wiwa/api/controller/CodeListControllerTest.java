package sk.janobono.wiwa.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import sk.janobono.wiwa.api.model.codelist.CodeListChangeWebDto;
import sk.janobono.wiwa.api.model.codelist.CodeListWebDto;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CodeListControllerTest extends BaseControllerTest {

    @Test
    void fullTest() {
        final String token = signIn(DEFAULT_MANAGER, PASSWORD).token();

        final HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        final List<CodeListWebDto> codeLists = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            final CodeListWebDto codeList = addCodeList(headers, new CodeListChangeWebDto("code-" + i, "name-" + i));
            codeLists.add(codeList);
        }

        codeLists.clear();
        codeLists.addAll(getCodeLists(headers, null, null, null, Pageable.unpaged()).stream().toList());

        for (final CodeListWebDto codeList : codeLists) {
            assertThat(codeList).usingRecursiveComparison().isEqualTo(getCodeList(headers, codeList.id()));
        }

        final List<CodeListWebDto> searchCodeLists = getCodeLists(headers, "name-", null, null, Pageable.unpaged()).stream().toList();
        assertThat(searchCodeLists).hasSize(10);

        final List<CodeListWebDto> codeCodeLists = getCodeLists(headers, null, "code-1", null, Pageable.unpaged()).stream().toList();
        assertThat(codeCodeLists).hasSize(1);

        final List<CodeListWebDto> nameCodeLists = getCodeLists(headers, null, null, "NAME", Pageable.unpaged()).stream().toList();
        assertThat(nameCodeLists).hasSize(10);

        final CodeListWebDto changedCodeList = setCodeList(headers, 1L, new CodeListChangeWebDto("code-x", "name-x"));
        assertThat(changedCodeList.code()).isEqualTo("code-x");
        assertThat(changedCodeList.name()).isEqualTo("name-x");

        codeLists.forEach(codeListSo -> deleteCodeList(headers, codeListSo.id()));
    }

    private CodeListWebDto getCodeList(final HttpHeaders headers, final Long id) {
        return getEntity(CodeListWebDto.class, headers, "/code-lists", id);
    }

    private Page<CodeListWebDto> getCodeLists(final HttpHeaders headers, final String searchField, final String code, final String name, final Pageable pageable) {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        addToParams(params, "searchField", searchField);
        addToParams(params, "code", code);
        addToParams(params, "name", name);
        return getEntities(CodeListWebDto.class, headers, "/code-lists", params, pageable);
    }

    private CodeListWebDto addCodeList(final HttpHeaders headers, final CodeListChangeWebDto data) {
        return addEntity(CodeListWebDto.class, headers, "/code-lists", data);
    }

    private CodeListWebDto setCodeList(final HttpHeaders headers, final Long id, final CodeListChangeWebDto data) {
        return setEntity(CodeListWebDto.class, headers, "/code-lists", id, data);
    }

    private void deleteCodeList(final HttpHeaders headers, final Long id) {
        deleteEntity(headers, "/code-lists", id);
    }
}
