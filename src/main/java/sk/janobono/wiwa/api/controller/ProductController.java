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
import sk.janobono.wiwa.api.component.WebImageUtil;
import sk.janobono.wiwa.api.model.ApplicationImageWeb;
import sk.janobono.wiwa.api.model.SingleValueBody;
import sk.janobono.wiwa.business.model.product.ProductDataSo;
import sk.janobono.wiwa.business.model.product.ProductSearchCriteriaSo;
import sk.janobono.wiwa.business.model.product.ProductSo;
import sk.janobono.wiwa.business.model.product.ProductUnitPriceSo;
import sk.janobono.wiwa.business.service.ProductService;
import sk.janobono.wiwa.model.ProductStockStatus;
import sk.janobono.wiwa.model.ProductType;
import sk.janobono.wiwa.model.Quantity;
import sk.janobono.wiwa.model.ResourceEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/products")
public class ProductController {

    private final ProductService productService;
    private final WebImageUtil webImageUtil;

    @GetMapping
    public Page<ProductSo> getProducts(
            @RequestParam(value = "search-field", required = false) final String searchField,
            @RequestParam(value = "type", required = false) final ProductType type,
            @RequestParam(value = "code", required = false) final String code,
            @RequestParam(value = "name", required = false) final String name,
            @RequestParam(value = "category-code", required = false) final String categoryCode,
            @RequestParam(value = "board-code", required = false) final String boardCode,
            @RequestParam(value = "structure-code", required = false) final String structureCode,
            @RequestParam(value = "stock-status", required = false) final ProductStockStatus productStockStatus,
            @RequestParam(value = "unit-price-from", required = false) final BigDecimal unitPriceFrom,
            @RequestParam(value = "unit-price-to", required = false) final BigDecimal unitPriceTo,
            @RequestParam(value = "thickness-value", required = false) final BigDecimal thicknessValue,
            @RequestParam(value = "thickness-unit", required = false) final String thicknessUnit,
            @RequestParam(value = "orientation", required = false) final Boolean orientation,
            final Pageable pageable
    ) {
        final ProductSearchCriteriaSo productSearchCriteriaSo = ProductSearchCriteriaSo.builder()
                .searchField(searchField)
                .type(type)
                .code(code)
                .name(name)
                .categoryCode(categoryCode)
                .boardCode(boardCode)
                .structureCode(structureCode)
                .productStockStatus(productStockStatus)
                .unitPriceFrom(unitPriceFrom)
                .unitPriceTo(unitPriceTo)
                .thickness(Optional.ofNullable(thicknessValue).isPresent() && Optional.ofNullable(thicknessUnit).filter(s -> !s.isBlank()).isPresent() ? new Quantity(thicknessValue, thicknessUnit) : null)
                .orientation(orientation)
                .build();
        return productService.getProducts(productSearchCriteriaSo, pageable);
    }

    @GetMapping("/{id}")
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

    @GetMapping("/{id}/product-images")
    public List<ApplicationImageWeb> getProductImages(@PathVariable("id") final Long id) {
        return productService.getProductImages(id).stream()
                .map(webImageUtil::toWeb)
                .toList();
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
    public ApplicationImageWeb setProductImage(@PathVariable("id") final Long id, @RequestParam("file") final MultipartFile multipartFile) {
        return webImageUtil.toWeb(productService.setProductImage(id, multipartFile));
    }

    @DeleteMapping("/{id}/product-images/{fileName}")
    @PreAuthorize("hasAnyAuthority('p2-admin', 'p2-manager')")
    public void deleteProductImage(@PathVariable("id") final Long id, @PathVariable("fileName") final String fileName) {
        productService.deleteProductImage(id, fileName);
    }

    @GetMapping("/{id}/product-unit-prices")
    public List<ProductUnitPriceSo> getProductUnitPrices(@PathVariable("id") final Long id) {
        return productService.getProductUnitPrices(id);
    }

    @PostMapping("/{id}/product-unit-prices")
    @PreAuthorize("hasAnyAuthority('p2-admin', 'p2-manager')")
    public List<ProductUnitPriceSo> setProductUnitPrices(@PathVariable("id") final Long id,
                                                         @Valid @RequestBody final SingleValueBody<List<ProductUnitPriceSo>> productUnitPrices) {
        return productService.setProductUnitPrices(id, productUnitPrices.value());
    }

    @GetMapping("/{id}/product-category-ids")
    @PreAuthorize("hasAnyAuthority('p2-admin', 'p2-manager')")
    public List<Long> getProductCategoryIds(@PathVariable("id") final Long id) {
        return productService.getProductCategoryIds(id);
    }

    @PostMapping("/{id}/product-category-ids")
    @PreAuthorize("hasAnyAuthority('p2-admin', 'p2-manager')")
    public List<Long> setProductCategoryIds(@PathVariable("id") final Long id,
                                            @Valid @RequestBody final SingleValueBody<List<Long>> productCategoryIds) {
        return productService.setProductCategoryIds(id, productCategoryIds.value());
    }
}
