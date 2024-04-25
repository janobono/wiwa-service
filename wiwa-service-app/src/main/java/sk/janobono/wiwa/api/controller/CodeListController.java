package sk.janobono.wiwa.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Operation(parameters = {
            @Parameter(in = ParameterIn.QUERY, name = "page", content = @Content(schema = @Schema(type = "integer"))),
            @Parameter(in = ParameterIn.QUERY, name = "size", content = @Content(schema = @Schema(type = "integer"))),
            @Parameter(in = ParameterIn.QUERY, name = "sort",
                    content = @Content(array = @ArraySchema(schema = @Schema(type = "string")))
            )
    })
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
    public CodeListWebDto getCodeList(@PathVariable("id") final long id) {
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
    public CodeListWebDto setCodeList(@PathVariable("id") final long id, @Valid @RequestBody final CodeListChangeWebDto codeListChange) {
        return codeListApiService.setCodeList(id, codeListChange);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public void deleteCodeList(@PathVariable("id") final long id) {
        codeListApiService.deleteCodeList(id);
    }
}
