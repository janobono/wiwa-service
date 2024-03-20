package sk.janobono.wiwa.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sk.janobono.wiwa.api.model.product.EdgeProductWebDto;
import sk.janobono.wiwa.api.model.product.ProductCategoryItemWebDto;
import sk.janobono.wiwa.api.service.EdgeProductApiService;
import sk.janobono.wiwa.model.ProductStockStatus;
import sk.janobono.wiwa.model.Unit;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/edge-products")
public class EdgeProductController {

    private final EdgeProductApiService edgeProductApiService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public Page<EdgeProductWebDto> getEdgeProducts(
            @RequestParam(value = "searchField", required = false) final String searchField,
            @RequestParam(value = "code", required = false) final String code,
            @RequestParam(value = "name", required = false) final String name,
            @RequestParam(value = "stockStatus", required = false) final ProductStockStatus stockStatus,
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
        return edgeProductApiService.getEdgeProducts(
                searchField,
                code,
                name,
                stockStatus,
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
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public EdgeProductWebDto getEdgeProduct(@PathVariable("id") final Long id) {
        return edgeProductApiService.getEdgeProduct(id);
    }

    @GetMapping(value = "/search-items")
    public List<ProductCategoryItemWebDto> getSearchItems() {
        return edgeProductApiService.getSearchItems();
    }
}
