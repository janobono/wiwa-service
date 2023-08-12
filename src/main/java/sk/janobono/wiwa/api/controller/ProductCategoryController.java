package sk.janobono.wiwa.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sk.janobono.wiwa.business.model.product.ProductCategoryDataSo;
import sk.janobono.wiwa.business.model.product.ProductCategorySearchCriteriaSo;
import sk.janobono.wiwa.business.model.product.ProductCategorySo;
import sk.janobono.wiwa.business.service.ProductCategoryService;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/product-categories")
public class ProductCategoryController {

    private final ProductCategoryService productCategoryService;

    @GetMapping
    public Page<ProductCategorySo> getProductCategories(
            @RequestParam(value = "root-categories", required = false) final Boolean rootCategories,
            @RequestParam(value = "parent-category-id", required = false) final Long parentCategoryId,
            @RequestParam(value = "search-field", required = false) final String searchField,
            @RequestParam(value = "code", required = false) final String code,
            @RequestParam(value = "name", required = false) final String name,
            @RequestParam(value = "tree-code", required = false) final String treeCode,
            final Pageable pageable
    ) {
        final ProductCategorySearchCriteriaSo productCategorySearchCriteriaSo = ProductCategorySearchCriteriaSo.builder()
                .rootCategories(rootCategories)
                .parentCategoryId(parentCategoryId)
                .searchField(searchField)
                .code(code)
                .name(name)
                .treeCode(treeCode)
                .build();
        return productCategoryService.getProductCategories(productCategorySearchCriteriaSo, pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public ProductCategorySo getProductCategory(@PathVariable("id") final Long id) {
        return productCategoryService.getProductCategory(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductCategorySo addProductCategory(@Valid @RequestBody final ProductCategoryDataSo productCategoryDataSo) {
        return productCategoryService.addProductCategory(productCategoryDataSo);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public ProductCategorySo setProductCategory(@PathVariable("id") final Long id, @Valid @RequestBody final ProductCategoryDataSo productCategoryDataSo) {
        return productCategoryService.setProductCategory(id, productCategoryDataSo);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public void deleteProductCategory(@PathVariable("id") final Long id) {
        productCategoryService.deleteProductCategory(id);
    }

    @PatchMapping("/{id}/move-up")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public ProductCategorySo moveProductCategoryUp(@PathVariable("id") final Long id) {
        return productCategoryService.moveProductCategoryUp(id);
    }

    @PatchMapping("/{id}/move-down")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public ProductCategorySo moveProductCategoryDown(@PathVariable("id") final Long id) {
        return productCategoryService.moveProductCategoryDown(id);
    }
}
