package sk.janobono.wiwa.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sk.janobono.wiwa.api.model.SingleValueBody;
import sk.janobono.wiwa.business.model.productconfig.ProductCategoryItemDataSo;
import sk.janobono.wiwa.business.model.productconfig.ProductCategoryItemSo;
import sk.janobono.wiwa.business.model.productconfig.ProductCategorySo;
import sk.janobono.wiwa.business.service.ProductConfigService;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/product-config")
public class ProductConfigController {

    private final ProductConfigService productConfigService;

    @GetMapping(value = "/vat-rate")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public SingleValueBody<BigDecimal> getVatRate() {
        return new SingleValueBody<>(productConfigService.getVatRate());
    }

    @PostMapping(value = "/vat-rate")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public SingleValueBody<BigDecimal> setVatRate(@Valid @RequestBody final SingleValueBody<BigDecimal> data) {
        return new SingleValueBody<>(productConfigService.setVatRate(data.value()));
    }

    @GetMapping(value = "/product-categories")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public List<ProductCategorySo> getProductCategories() {
        return productConfigService.getProductCategories();
    }

    @PostMapping(value = "/product-categories")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public List<ProductCategorySo> setProductCategories(@Valid @RequestBody final List<Long> data) {
        return productConfigService.setProductCategories(data);
    }

    @GetMapping(value = "/board-category-item")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public ProductCategoryItemSo getBoardCategoryItem() {
        return productConfigService.getBoardCategoryItem();
    }

    @PostMapping(value = "/board-category-item")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public ProductCategoryItemSo setBoardCategoryItem(@Valid @RequestBody final ProductCategoryItemDataSo data) {
        return productConfigService.setBoardCategoryItem(data);
    }

    @GetMapping(value = "/edge-category-item")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public ProductCategoryItemSo getEdgeCategoryItem() {
        return productConfigService.getEdgeCategoryItem();
    }

    @PostMapping(value = "/edge-category-item")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public ProductCategoryItemSo setEdgeCategoryItem(@Valid @RequestBody final ProductCategoryItemDataSo data) {
        return productConfigService.setEdgeCategoryItem(data);
    }

    @GetMapping(value = "/service-category-item")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public ProductCategoryItemSo getServiceCategoryItem() {
        return productConfigService.getServiceCategoryItem();
    }

    @PostMapping(value = "/service-category-item")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public ProductCategoryItemSo setServiceCategoryItem(@Valid @RequestBody final ProductCategoryItemDataSo data) {
        return productConfigService.setServiceCategoryItem(data);
    }

    @GetMapping(value = "/search-items")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public List<ProductCategoryItemSo> getSearchItems() {
        return productConfigService.getSearchItems();
    }

    @PostMapping(value = "/search-items")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public List<ProductCategoryItemSo> setSearchItems(@Valid @RequestBody final List<ProductCategoryItemDataSo> data) {
        return productConfigService.setSearchItems(data);
    }
}
