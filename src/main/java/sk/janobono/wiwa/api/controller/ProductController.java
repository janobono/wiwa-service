package sk.janobono.wiwa.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sk.janobono.wiwa.api.model.product.ProductCategoryItemChangeWebDto;
import sk.janobono.wiwa.api.model.product.ProductChangeWebDto;
import sk.janobono.wiwa.api.model.product.ProductUnitPriceChangeWebDto;
import sk.janobono.wiwa.api.model.product.ProductWebDto;
import sk.janobono.wiwa.api.service.ProductApiService;
import sk.janobono.wiwa.model.ProductStockStatus;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/products")
public class ProductController {

    private final ProductApiService productApiService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public Page<ProductWebDto> getProducts(
            @RequestParam(value = "searchField", required = false) final String searchField,
            @RequestParam(value = "code", required = false) final String code,
            @RequestParam(value = "name", required = false) final String name,
            @RequestParam(value = "stockStatus", required = false) final ProductStockStatus stockStatus,
            @RequestParam(value = "codeListItems", required = false) final List<String> codeListItems,
            final Pageable pageable
    ) {
        return productApiService.getProducts(searchField, code, name, stockStatus, codeListItems, pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public ProductWebDto getProduct(@PathVariable("id") final Long id) {
        return productApiService.getProduct(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductWebDto addProduct(@Valid @RequestBody final ProductChangeWebDto productChange) {
        return productApiService.addProduct(productChange);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public ProductWebDto setProduct(@PathVariable("id") final Long id, @Valid @RequestBody final ProductChangeWebDto data) {
        return productApiService.setProduct(id, data);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public void deleteProduct(@PathVariable("id") final Long id) {
        productApiService.deleteProduct(id);
    }

    @PostMapping("/{id}/images")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public ProductWebDto setProductImage(@PathVariable("id") final Long id, @RequestParam("file") final MultipartFile multipartFile) {
        return productApiService.setProductImage(id, multipartFile);
    }

    @DeleteMapping("/{id}/images/{fileName}")
    @PreAuthorize("hasAnyAuthority('p2-admin', 'p2-manager')")
    public ProductWebDto deleteProductImage(@PathVariable("id") final Long id, @PathVariable("fileName") final String fileName) {
        return productApiService.deleteProductImage(id, fileName);
    }

    @PostMapping("/{id}/unit-prices")
    @PreAuthorize("hasAnyAuthority('p2-admin', 'p2-manager')")
    public ProductWebDto setProductUnitPrices(@PathVariable("id") final Long id,
                                              @Valid @RequestBody final List<ProductUnitPriceChangeWebDto> productUnitPrices) {
        return productApiService.setProductUnitPrices(id, productUnitPrices);
    }

    @PostMapping("/{id}/category-items")
    @PreAuthorize("hasAnyAuthority('p2-admin', 'p2-manager')")
    public ProductWebDto setProductCategoryItems(@PathVariable("id") final Long id, @RequestBody final List<ProductCategoryItemChangeWebDto> categoryItems) {
        return productApiService.setProductCategoryItems(id, categoryItems);
    }
}
