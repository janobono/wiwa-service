package sk.janobono.wiwa.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sk.janobono.wiwa.api.model.SingleValueBody;
import sk.janobono.wiwa.business.model.product.ProductDataSo;
import sk.janobono.wiwa.business.model.product.ProductSearchCriteriaSo;
import sk.janobono.wiwa.business.model.product.ProductSo;
import sk.janobono.wiwa.business.model.product.ProductUnitPriceDataSo;
import sk.janobono.wiwa.business.service.ProductService;
import sk.janobono.wiwa.model.ProductStockStatus;
import sk.janobono.wiwa.model.ResourceEntity;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public Page<ProductSo> getProducts(
            @RequestParam(value = "searchField", required = false) final String searchField,
            @RequestParam(value = "code", required = false) final String code,
            @RequestParam(value = "name", required = false) final String name,
            @RequestParam(value = "stockStatus", required = false) final ProductStockStatus stockStatus,
            @RequestParam(value = "codeListItems", required = false) final List<String> codeListItems,
            final Pageable pageable
    ) {
        final ProductSearchCriteriaSo criteria = ProductSearchCriteriaSo.builder()
                .searchField(searchField)
                .code(code)
                .name(name)
                .stockStatus(stockStatus)
                .codeListItems(codeListItems)
                .build();
        return productService.getProducts(criteria, pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public ProductSo getProduct(@PathVariable("id") final Long id) {
        return productService.getProduct(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductSo addProduct(@Valid @RequestBody final ProductDataSo productDataSo) {
        return productService.addProduct(productDataSo);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public ProductSo setProduct(@PathVariable("id") final Long id, @Valid @RequestBody final ProductDataSo productDataSo) {
        return productService.setProduct(id, productDataSo);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public void deleteProduct(@PathVariable("id") final Long id) {
        productService.deleteProduct(id);
    }

    @GetMapping("/{id}/product-images/{fileName}")
    public ResponseEntity<Resource> getProductImage(@PathVariable("id") final Long id, @PathVariable("fileName") final String fileName) {
        final ResourceEntity resourceEntity = productService.getProductImage(id, fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(resourceEntity.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resourceEntity.fileName() + "\"")
                .body(resourceEntity.resource());
    }

    @PostMapping("/{id}/product-images")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public ProductSo setProductImage(@PathVariable("id") final Long id, @RequestParam("file") final MultipartFile multipartFile) {
        return productService.setProductImage(id, multipartFile);
    }

    @DeleteMapping("/{id}/product-images/{fileName}")
    @PreAuthorize("hasAnyAuthority('p2-admin', 'p2-manager')")
    public ProductSo deleteProductImage(@PathVariable("id") final Long id, @PathVariable("fileName") final String fileName) {
        return productService.deleteProductImage(id, fileName);
    }

    @PostMapping("/{id}/product-unit-prices")
    @PreAuthorize("hasAnyAuthority('p2-admin', 'p2-manager')")
    public ProductSo setProductUnitPrices(@PathVariable("id") final Long id,
                                          @Valid @RequestBody final List<ProductUnitPriceDataSo> productUnitPrices) {
        return productService.setProductUnitPrices(id, productUnitPrices);
    }

    @PostMapping("/{id}/code-list-items")
    @PreAuthorize("hasAnyAuthority('p2-admin', 'p2-manager')")
    public ProductSo setProductCodeListItems(@PathVariable("id") final Long id, @RequestBody final List<Long> itemIds) {
        return productService.setProductCodeListItems(id, itemIds);
    }
}
