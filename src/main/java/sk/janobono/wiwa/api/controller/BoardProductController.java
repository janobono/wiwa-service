package sk.janobono.wiwa.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import sk.janobono.wiwa.api.model.product.BoardProductWebDto;
import sk.janobono.wiwa.api.model.product.ProductCategoryItemWebDto;
import sk.janobono.wiwa.api.service.BoardProductApiService;
import sk.janobono.wiwa.model.ProductStockStatus;
import sk.janobono.wiwa.model.Unit;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/board-products")
public class BoardProductController {

    private final BoardProductApiService boardProductApiService;

    @GetMapping
    public Page<BoardProductWebDto> getBoardProducts(
            @RequestParam(value = "searchField", required = false) final String searchField,
            @RequestParam(value = "code", required = false) final String code,
            @RequestParam(value = "name", required = false) final String name,
            @RequestParam(value = "stockStatus", required = false) final ProductStockStatus stockStatus,
            @RequestParam(value = "boardCode", required = false) final String boardCode,
            @RequestParam(value = "structureCode", required = false) final String structureCode,
            @RequestParam(value = "orientation", required = false) final Boolean orientation,
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
        return boardProductApiService.getBoardProducts(
                searchField,
                code,
                name,
                stockStatus,
                boardCode,
                structureCode,
                orientation,
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
    public BoardProductWebDto getBoardProduct(@PathVariable("id") final Long id) {
        return boardProductApiService.getBoardProduct(id);
    }

    @GetMapping(value = "/search-items")
    public List<ProductCategoryItemWebDto> getSearchItems() {
        return boardProductApiService.getSearchItems();
    }
}
