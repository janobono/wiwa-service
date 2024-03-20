package sk.janobono.wiwa.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sk.janobono.wiwa.api.model.product.FreeSaleProductWebDto;
import sk.janobono.wiwa.api.model.product.ProductCategoryItemWebDto;
import sk.janobono.wiwa.api.service.FreeSaleProductApiService;
import sk.janobono.wiwa.model.ProductStockStatus;
import sk.janobono.wiwa.model.Unit;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/free-sale-products")
public class FreeSaleProductController {

    private final FreeSaleProductApiService freeSaleProductApiService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public Page<FreeSaleProductWebDto> getFreeSaleProducts(
            @RequestParam(value = "searchField", required = false) final String searchField,
            @RequestParam(value = "code", required = false) final String code,
            @RequestParam(value = "name", required = false) final String name,
            @RequestParam(value = "stockStatus", required = false) final ProductStockStatus stockStatus,
            @RequestParam(value = "lengthFrom", required = false) final BigDecimal lengthFrom,
            @RequestParam(value = "lengthTo", required = false) final BigDecimal lengthTo,
            @RequestParam(value = "lengthUnit", required = false) final Unit lengthUnit,
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
        return freeSaleProductApiService.getFreeSaleProducts(
                searchField,
                code,
                name,
                stockStatus,
                lengthFrom,
                lengthTo,
                lengthUnit,
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
    public FreeSaleProductWebDto getFreeSaleProduct(@PathVariable("id") final Long id) {
        return freeSaleProductApiService.getFreeSaleProduct(id);
    }

    @GetMapping(value = "/search-items")
    public List<ProductCategoryItemWebDto> getSearchItems() {
        return freeSaleProductApiService.getSearchItems();
    }
}
