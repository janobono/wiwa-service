package sk.janobono.wiwa.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sk.janobono.wiwa.api.model.SingleValueBody;
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
    public List<Long> getProductCategories() {
        return productConfigService.getProductCategories();
    }

    @PostMapping(value = "/product-categories")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public List<Long> setProductCategories(@Valid @RequestBody final List<Long> data) {
        return productConfigService.setProductCategories(data);
    }

    @GetMapping(value = "/board-category-id")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public SingleValueBody<Long> getBoardCategoryId() {
        return new SingleValueBody<>(productConfigService.getBoardCategoryId());
    }

    @PostMapping(value = "/board-category-id")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public SingleValueBody<Long> setBoardCategoryId(@Valid @RequestBody final SingleValueBody<Long> data) {
        return new SingleValueBody<>(productConfigService.setBoardCategoryId(data.value()));
    }

    @GetMapping(value = "/edge-category-id")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public SingleValueBody<Long> getEdgeCategoryId() {
        return new SingleValueBody<>(productConfigService.getEdgeCategoryId());
    }

    @PostMapping(value = "/edge-category-id")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public SingleValueBody<Long> setEdgeCategoryId(@Valid @RequestBody final SingleValueBody<Long> data) {
        return new SingleValueBody<>(productConfigService.setEdgeCategoryId(data.value()));
    }

    @GetMapping(value = "/service-category-id")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public SingleValueBody<Long> getServiceCategoryId() {
        return new SingleValueBody<>(productConfigService.getServiceCategoryId());
    }

    @PostMapping(value = "/service-category-id")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public SingleValueBody<Long> setServiceCategoryId(@Valid @RequestBody final SingleValueBody<Long> data) {
        return new SingleValueBody<>(productConfigService.setServiceCategoryId(data.value()));
    }

    @GetMapping(value = "/search-categories")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public List<Long> getSearchCategories() {
        return productConfigService.getSearchCategories();
    }

    @PostMapping(value = "/search-categories")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public List<Long> setSearchCategories(@Valid @RequestBody final List<Long> data) {
        return productConfigService.setSearchCategories(data);
    }
}
