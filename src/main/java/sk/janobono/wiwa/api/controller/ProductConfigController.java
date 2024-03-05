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

    @GetMapping(value = "/service-category-item")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public ProductCategoryItemWebDto getServiceCategoryItem() {
        return productConfigApiService.getServiceCategoryItem();
    }

    @PostMapping(value = "/service-category-item")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public ProductCategoryItemWebDto setServiceCategoryItem(@Valid @RequestBody final ProductCategoryItemChangeWebDto productCategoryItemChange) {
        return productConfigApiService.setServiceCategoryItem(productCategoryItemChange);
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

    @GetMapping(value = "/search-items")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public List<ProductCategoryItemWebDto> getSearchItems() {
        return productConfigApiService.getSearchItems();
    }

    @PostMapping(value = "/search-items")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public List<ProductCategoryItemWebDto> setSearchItems(@Valid @RequestBody final List<ProductCategoryItemChangeWebDto> data) {
        return productConfigApiService.setSearchItems(data);
    }
}
