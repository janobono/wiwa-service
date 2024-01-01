package sk.janobono.wiwa.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sk.janobono.wiwa.business.model.codelist.CodeListItemDataSo;
import sk.janobono.wiwa.business.model.codelist.CodeListItemSearchCriteriaSo;
import sk.janobono.wiwa.business.model.codelist.CodeListItemSo;
import sk.janobono.wiwa.business.service.CodeListItemService;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/code-list-items")
public class CodeListItemController {

    private final CodeListItemService codeListItemService;

    @GetMapping
    public Page<CodeListItemSo> getCodeListItems(
            @RequestParam(value = "codeListId", required = false) final Long codeListId,
            @RequestParam(value = "root", required = false) final Boolean root,
            @RequestParam(value = "parentId", required = false) final Long parentId,
            @RequestParam(value = "searchField", required = false) final String searchField,
            @RequestParam(value = "code", required = false) final String code,
            @RequestParam(value = "value", required = false) final String value,
            @RequestParam(value = "treeCode", required = false) final String treeCode,
            final Pageable pageable
    ) {
        final CodeListItemSearchCriteriaSo criteria = CodeListItemSearchCriteriaSo.builder()
                .codeListId(codeListId)
                .root(root)
                .parentId(parentId)
                .searchField(searchField)
                .code(code)
                .value(value)
                .treeCode(treeCode)
                .build();
        return codeListItemService.getCodeListItems(criteria, pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public CodeListItemSo getCodeListItem(@PathVariable("id") final Long id) {
        return codeListItemService.getCodeListItem(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    @ResponseStatus(HttpStatus.CREATED)
    public CodeListItemSo addCodeListItem(@Valid @RequestBody final CodeListItemDataSo data) {
        return codeListItemService.addCodeListItem(data);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public CodeListItemSo setCodeListItem(@PathVariable("id") final Long id, @Valid @RequestBody final CodeListItemDataSo data) {
        return codeListItemService.setCodeListItem(id, data);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public void deleteCodeListItem(@PathVariable("id") final Long id) {
        codeListItemService.deleteCodeListItem(id);
    }

    @PatchMapping("/{id}/move-up")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public CodeListItemSo moveCodeListItemUp(@PathVariable("id") final Long id) {
        return codeListItemService.moveCodeListItemUp(id);
    }

    @PatchMapping("/{id}/move-down")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public CodeListItemSo moveCodeListItemDown(@PathVariable("id") final Long id) {
        return codeListItemService.moveCodeListItemDown(id);
    }
}
