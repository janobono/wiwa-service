package sk.janobono.wiwa.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sk.janobono.wiwa.api.model.codelist.CodeListItemChangeWebDto;
import sk.janobono.wiwa.api.model.codelist.CodeListItemWebDto;
import sk.janobono.wiwa.api.service.CodeListItemApiService;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/code-list-items")
public class CodeListItemController {

    private final CodeListItemApiService codeListItemApiService;

    @GetMapping
    public Page<CodeListItemWebDto> getCodeListItems(
            @RequestParam(value = "codeListId", required = false) final Long codeListId,
            @RequestParam(value = "root", required = false) final Boolean root,
            @RequestParam(value = "parentId", required = false) final Long parentId,
            @RequestParam(value = "searchField", required = false) final String searchField,
            @RequestParam(value = "code", required = false) final String code,
            @RequestParam(value = "value", required = false) final String value,
            @RequestParam(value = "treeCode", required = false) final String treeCode,
            final Pageable pageable
    ) {
        return codeListItemApiService.getCodeListItems(codeListId, root, parentId, searchField, code, value, treeCode, pageable);
    }

    @GetMapping("/{id}")
    public CodeListItemWebDto getCodeListItem(@PathVariable("id") final Long id) {
        return codeListItemApiService.getCodeListItem(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    @ResponseStatus(HttpStatus.CREATED)
    public CodeListItemWebDto addCodeListItem(@Valid @RequestBody final CodeListItemChangeWebDto codeListItemChange) {
        return codeListItemApiService.addCodeListItem(codeListItemChange);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public CodeListItemWebDto setCodeListItem(@PathVariable("id") final Long id, @Valid @RequestBody final CodeListItemChangeWebDto codeListItemChange) {
        return codeListItemApiService.setCodeListItem(id, codeListItemChange);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public void deleteCodeListItem(@PathVariable("id") final Long id) {
        codeListItemApiService.deleteCodeListItem(id);
    }

    @PatchMapping("/{id}/move-up")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public CodeListItemWebDto moveCodeListItemUp(@PathVariable("id") final Long id) {
        return codeListItemApiService.moveCodeListItemUp(id);
    }

    @PatchMapping("/{id}/move-down")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public CodeListItemWebDto moveCodeListItemDown(@PathVariable("id") final Long id) {
        return codeListItemApiService.moveCodeListItemDown(id);
    }
}
