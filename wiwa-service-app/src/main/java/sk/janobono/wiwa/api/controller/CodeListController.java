package sk.janobono.wiwa.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sk.janobono.wiwa.api.model.codelist.CodeListChangeWebDto;
import sk.janobono.wiwa.api.model.codelist.CodeListWebDto;
import sk.janobono.wiwa.api.service.CodeListApiService;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/code-lists")
public class CodeListController {

    private final CodeListApiService codeListApiService;

    @GetMapping
    public Page<CodeListWebDto> getCodeLists(
            @RequestParam(value = "searchField", required = false) final String searchField,
            @RequestParam(value = "code", required = false) final String code,
            @RequestParam(value = "name", required = false) final String name,
            final Pageable pageable
    ) {
        return codeListApiService.getCodeLists(searchField, code, name, pageable);
    }

    @GetMapping("/{id}")
    public CodeListWebDto getCodeList(@PathVariable("id") final Long id) {
        return codeListApiService.getCodeList(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    @ResponseStatus(HttpStatus.CREATED)
    public CodeListWebDto addCodeList(@Valid @RequestBody final CodeListChangeWebDto codeListChange) {
        return codeListApiService.addCodeList(codeListChange);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public CodeListWebDto setCodeList(@PathVariable("id") final Long id, @Valid @RequestBody final CodeListChangeWebDto codeListChange) {
        return codeListApiService.setCodeList(id, codeListChange);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public void deleteCodeList(@PathVariable("id") final Long id) {
        codeListApiService.deleteCodeList(id);
    }
}
