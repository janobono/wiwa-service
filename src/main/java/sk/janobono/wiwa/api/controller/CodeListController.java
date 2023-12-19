package sk.janobono.wiwa.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sk.janobono.wiwa.business.model.codelist.*;
import sk.janobono.wiwa.business.service.CodeListService;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/code-lists")
public class CodeListController {

    private final CodeListService codeListService;

    @GetMapping
    public Page<CodeListSo> getCodeLists(
            @RequestParam(value = "search-field", required = false) final String searchField,
            @RequestParam(value = "code", required = false) final String code,
            @RequestParam(value = "name", required = false) final String name,
            final Pageable pageable
    ) {
        final CodeListSearchCriteriaSo criteria = CodeListSearchCriteriaSo.builder()
                .searchField(searchField)
                .code(code)
                .name(name)
                .build();
        return codeListService.getCodeLists(criteria, pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public CodeListSo getCodeList(@PathVariable("id") final Long id) {
        return codeListService.getCodeList(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    @ResponseStatus(HttpStatus.CREATED)
    public CodeListSo addCodeList(@Valid @RequestBody final CodeListDataSo data) {
        return codeListService.addCodeList(data);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public CodeListSo setCodeList(@PathVariable("id") final Long id, @Valid @RequestBody final CodeListDataSo data) {
        return codeListService.setCodeList(id, data);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public void deleteCodeList(@PathVariable("id") final Long id) {
        codeListService.deleteCodeList(id);
    }

    @GetMapping("/{id}/items")
    public Page<CodeListItemSo> getCodeListItems(
            @PathVariable("id") final Long id,
            @RequestParam(value = "root", required = false) final Boolean root,
            @RequestParam(value = "parent-id", required = false) final Long parentId,
            @RequestParam(value = "search-field", required = false) final String searchField,
            @RequestParam(value = "code", required = false) final String code,
            @RequestParam(value = "value", required = false) final String value,
            @RequestParam(value = "tree-code", required = false) final String treeCode,
            final Pageable pageable
    ) {
        final CodeListItemSearchCriteriaSo criteria = CodeListItemSearchCriteriaSo.builder()
                .codeListId(id)
                .root(root)
                .parentId(parentId)
                .searchField(searchField)
                .code(code)
                .value(value)
                .treeCode(treeCode)
                .build();
        return codeListService.getCodeListItems(criteria, pageable);
    }

    @GetMapping("/{id}/items/{itemId}")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public CodeListItemSo getCodeListItem(@PathVariable("id") final Long id, @PathVariable("itemId") final Long itemId) {
        return codeListService.getCodeListItem(id, itemId);
    }

    @PostMapping("/{id}/items")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    @ResponseStatus(HttpStatus.CREATED)
    public CodeListItemSo addCodeListItem(@PathVariable("id") final Long id, @Valid @RequestBody final CodeListItemDataSo data) {
        return codeListService.addCodeListItem(id, data);
    }

    @PutMapping("/{id}/items/{itemId}")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public CodeListItemSo setCodeListItem(@PathVariable("id") final Long id, @PathVariable("itemId") final Long itemId, @Valid @RequestBody final CodeListItemDataSo data) {
        return codeListService.setCodeListItem(id, itemId, data);
    }

    @DeleteMapping("/{id}/items/{itemId}")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public void deleteCodeListItem(@PathVariable("id") final Long id, @PathVariable("itemId") final Long itemId) {
        codeListService.deleteCodeListItem(id, itemId);
    }

    @PatchMapping("/{id}/items/{itemId}/move-up")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public CodeListItemSo moveCodeListItemUp(@PathVariable("id") final Long id, @PathVariable("itemId") final Long itemId) {
        return codeListService.moveCodeListItemUp(id, itemId);
    }

    @PatchMapping("/{id}/items/{itemId}/move-down")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public CodeListItemSo moveCodeListItemDown(@PathVariable("id") final Long id, @PathVariable("itemId") final Long itemId) {
        return codeListService.moveCodeListItemDown(id, itemId);
    }
}
