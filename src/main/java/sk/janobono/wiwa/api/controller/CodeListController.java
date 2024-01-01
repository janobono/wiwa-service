package sk.janobono.wiwa.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sk.janobono.wiwa.business.model.codelist.CodeListDataSo;
import sk.janobono.wiwa.business.model.codelist.CodeListSearchCriteriaSo;
import sk.janobono.wiwa.business.model.codelist.CodeListSo;
import sk.janobono.wiwa.business.service.CodeListService;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/code-lists")
public class CodeListController {

    private final CodeListService codeListService;

    @GetMapping
    public Page<CodeListSo> getCodeLists(
            @RequestParam(value = "searchField", required = false) final String searchField,
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
}
