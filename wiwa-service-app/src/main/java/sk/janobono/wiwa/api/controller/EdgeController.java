package sk.janobono.wiwa.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sk.janobono.wiwa.api.model.edge.EdgeCategoryItemChangeWebDto;
import sk.janobono.wiwa.api.model.edge.EdgeChangeWebDto;
import sk.janobono.wiwa.api.model.edge.EdgeWebDto;
import sk.janobono.wiwa.api.service.EdgeApiService;
import sk.janobono.wiwa.model.Unit;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/edges")
public class EdgeController {

    private final EdgeApiService edgeApiService;

    @GetMapping
    public Page<EdgeWebDto> getEdges(
            @RequestParam(value = "searchField", required = false) final String searchField,
            @RequestParam(value = "code", required = false) final String code,
            @RequestParam(value = "name", required = false) final String name,
            @RequestParam(value = "widthFrom", required = false) final BigDecimal widthFrom,
            @RequestParam(value = "widthTo", required = false) final BigDecimal widthTo,
            @RequestParam(value = "widthUnit", required = false) final Unit widthUnit,
            @RequestParam(value = "thicknessFrom", required = false) final BigDecimal thicknessFrom,
            @RequestParam(value = "thicknessTo", required = false) final BigDecimal thicknessTo,
            @RequestParam(value = "thicknessUnit", required = false) final Unit thicknessUnit,
            @RequestParam(value = "priceFrom", required = false) final BigDecimal priceFrom,
            @RequestParam(value = "priceTo", required = false) final BigDecimal priceTo,
            @RequestParam(value = "priceUnit", required = false) final Unit priceUnit,
            @RequestParam(value = "codeListItems", required = false) final List<String> codeListItems,
            final Pageable pageable
    ) {
        return edgeApiService.getEdges(
                searchField,
                code,
                name,
                widthFrom,
                widthTo,
                widthUnit,
                thicknessFrom,
                thicknessTo,
                thicknessUnit,
                priceFrom,
                priceTo,
                priceUnit,
                codeListItems,
                pageable
        );
    }

    @GetMapping("/{id}")
    public EdgeWebDto getEdge(@PathVariable("id") final Long id) {
        return edgeApiService.getEdge(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    @ResponseStatus(HttpStatus.CREATED)
    public EdgeWebDto addEdge(@Valid @RequestBody final EdgeChangeWebDto edgeChange) {
        return edgeApiService.addEdge(edgeChange);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public EdgeWebDto setEdge(@PathVariable("id") final Long id, @Valid @RequestBody final EdgeChangeWebDto edgeChange) {
        return edgeApiService.setEdge(id, edgeChange);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public void deleteEdge(@PathVariable("id") final Long id) {
        edgeApiService.deleteEdge(id);
    }

    @PostMapping("/{id}/images")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public EdgeWebDto setEdgeImage(@PathVariable("id") final Long id, @RequestParam("file") final MultipartFile multipartFile) {
        return edgeApiService.setEdgeImage(id, multipartFile);
    }

    @DeleteMapping("/{id}/images/{fileName}")
    @PreAuthorize("hasAnyAuthority('p2-admin', 'p2-manager')")
    public EdgeWebDto deleteEdgeImage(@PathVariable("id") final Long id, @PathVariable("fileName") final String fileName) {
        return edgeApiService.deleteEdgeImage(id, fileName);
    }

    @PostMapping("/{id}/category-items")
    @PreAuthorize("hasAnyAuthority('p2-admin', 'p2-manager')")
    public EdgeWebDto setEdgeCategoryItems(@PathVariable("id") final Long id, @RequestBody final List<EdgeCategoryItemChangeWebDto> categoryItems) {
        return edgeApiService.setEdgeCategoryItems(id, categoryItems);
    }
}
