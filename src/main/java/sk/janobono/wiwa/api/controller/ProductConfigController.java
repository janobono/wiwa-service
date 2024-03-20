package sk.janobono.wiwa.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sk.janobono.wiwa.api.model.SingleValueBodyWebDto;
import sk.janobono.wiwa.api.model.product.ProductCategoryChangeWebDto;
import sk.janobono.wiwa.api.model.product.ProductCategoryItemChangeWebDto;
import sk.janobono.wiwa.api.model.product.ProductCategoryItemWebDto;
import sk.janobono.wiwa.api.model.product.ProductCategoryWebDto;
import sk.janobono.wiwa.api.service.ProductConfigApiService;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/product-config")
public class ProductConfigController {

    private final ProductConfigApiService productConfigApiService;

    @GetMapping(value = "/vat-rate")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public SingleValueBodyWebDto<BigDecimal> getVatRate() {
        return productConfigApiService.getVatRate();
    }

    @PostMapping(value = "/vat-rate")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public SingleValueBodyWebDto<BigDecimal> setVatRate(@Valid @RequestBody final SingleValueBodyWebDto<BigDecimal> singleValueBody) {
        return productConfigApiService.setVatRate(singleValueBody);
    }

    @GetMapping(value = "/product-categories")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public List<ProductCategoryWebDto> getProductCategories() {
        return productConfigApiService.getProductCategories();
    }

    @PostMapping(value = "/product-categories")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public List<ProductCategoryWebDto> setProductCategories(@Valid @RequestBody final List<ProductCategoryChangeWebDto> data) {
        return productConfigApiService.setProductCategories(data);
    }

    @GetMapping(value = "/board-category-item")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public ProductCategoryItemWebDto getBoardCategoryItem() {
        return productConfigApiService.getBoardCategoryItem();
    }

    @PostMapping(value = "/board-category-item")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public ProductCategoryItemWebDto setBoardCategoryItem(@Valid @RequestBody final ProductCategoryItemChangeWebDto productCategoryItemChange) {
        return productConfigApiService.setBoardCategoryItem(productCategoryItemChange);
    }

    @PostMapping(value = "/board-search-items")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public List<ProductCategoryItemWebDto> setBoardSearchItems(@Valid @RequestBody final List<ProductCategoryItemChangeWebDto> data) {
        return productConfigApiService.setBoardSearchItems(data);
    }

    @GetMapping(value = "/edge-category-item")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public ProductCategoryItemWebDto getEdgeCategoryItem() {
        return productConfigApiService.getEdgeCategoryItem();
    }

    @PostMapping(value = "/edge-category-item")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public ProductCategoryItemWebDto setEdgeCategoryItem(@Valid @RequestBody final ProductCategoryItemChangeWebDto productCategoryItemChange) {
        return productConfigApiService.setEdgeCategoryItem(productCategoryItemChange);
    }

    @PostMapping(value = "/edge-search-items")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public List<ProductCategoryItemWebDto> setEdgeSearchItems(@Valid @RequestBody final List<ProductCategoryItemChangeWebDto> data) {
        return productConfigApiService.setEdgeSearchItems(data);
    }

    @GetMapping(value = "/free-sale-category-item")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public ProductCategoryItemWebDto getFreeSaleCategoryItem() {
        return productConfigApiService.getFreeSaleCategoryItem();
    }

    @PostMapping(value = "/free-sale-category-item")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public ProductCategoryItemWebDto setFreeSaleCategoryItem(@Valid @RequestBody final ProductCategoryItemChangeWebDto productCategoryItemChange) {
        return productConfigApiService.setFreeSaleCategoryItem(productCategoryItemChange);
    }

    @PostMapping(value = "/free-sale-search-items")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public List<ProductCategoryItemWebDto> setFreeSaleSearchItems(@Valid @RequestBody final List<ProductCategoryItemChangeWebDto> data) {
        return productConfigApiService.setFreeSaleSearchItems(data);
    }
}
